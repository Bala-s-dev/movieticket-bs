package com.movieticket.service;

import com.movieticket.enums.BookingStatus;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Booking;
import com.movieticket.model.Movie;
import com.movieticket.model.Screen;
import com.movieticket.model.Show;
import com.movieticket.model.Theatre;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.MovieRepository;
import com.movieticket.repository.ScreenRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.repository.TheatreRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final MovieRepository movieRepository;

    public ReportService(BookingRepository bookingRepository, ShowRepository showRepository,
                         ScreenRepository screenRepository, TheatreRepository theatreRepository,
                         MovieRepository movieRepository) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.screenRepository = screenRepository;
        this.theatreRepository = theatreRepository;
        this.movieRepository = movieRepository;
    }

    /** One line of a revenue breakdown (per movie or per theatre). */
    public static class RevenueLine {
        private final String label;
        private double amount;
        private int ticketsSold;
        private int bookingCount;

        RevenueLine(String label) {
            this.label = label;
        }

        public String getLabel() { return label; }
        public double getAmount() { return amount; }
        public int getTicketsSold() { return ticketsSold; }
        public int getBookingCount() { return bookingCount; }

        void add(double amount, int seats) {
            this.amount += amount;
            this.ticketsSold += seats;
            this.bookingCount += 1;
        }
    }

    public static class RevenueReport {
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private double totalRevenue;
        private int totalBookings;
        private int totalTicketsSold;
        private final Map<Long, RevenueLine> byMovie = new LinkedHashMap<>();
        private final Map<Long, RevenueLine> byTheatre = new LinkedHashMap<>();

        RevenueReport(LocalDate fromDate, LocalDate toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public LocalDate getFromDate() { return fromDate; }
        public LocalDate getToDate() { return toDate; }
        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalBookings() { return totalBookings; }
        public int getTotalTicketsSold() { return totalTicketsSold; }

        public List<RevenueLine> getByMovieSorted() {
            return byMovie.values().stream()
                    .sorted(Comparator.comparingDouble(RevenueLine::getAmount).reversed())
                    .collect(Collectors.toList());
        }

        public List<RevenueLine> getByTheatreSorted() {
            return byTheatre.values().stream()
                    .sorted(Comparator.comparingDouble(RevenueLine::getAmount).reversed())
                    .collect(Collectors.toList());
        }
    }

    public RevenueReport generateRevenueReport(long adminId, LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new ValidationException("Both from date and to date are required.");
        }
        if (fromDate.isAfter(toDate)) {
            throw new ValidationException("From date must not be after to date.");
        }

        LocalDateTime rangeStart = fromDate.atStartOfDay();
        LocalDateTime rangeEnd = toDate.atTime(23, 59, 59);

        // Resolve the set of screens/shows this admin actually owns.
        Set<Long> ownedTheatreIds = theatreRepository.findByAdminId(adminId).stream()
                .map(Theatre::getTheatreId)
                .collect(Collectors.toSet());

        Map<Long, Screen> ownedScreensById = screenRepository.findAll().stream()
                .filter(s -> ownedTheatreIds.contains(s.getTheatreId()))
                .collect(Collectors.toMap(Screen::getScreenId, s -> s, (a, b) -> a));

        Map<Long, Show> ownedShowsById = showRepository.findAll().stream()
                .filter(sh -> ownedScreensById.containsKey(sh.getScreenId()))
                .collect(Collectors.toMap(Show::getShowId, sh -> sh, (a, b) -> a));

        RevenueReport report = new RevenueReport(fromDate, toDate);

        // Cache theatre/movie lookups to avoid repeated repository scans.
        Map<Long, Theatre> theatreCache = new HashMap<>();
        Map<Long, Movie> movieCache = new HashMap<>();

        List<Booking> allBookings = bookingRepository.findAll();
        for (Booking booking : allBookings) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                continue;
            }
            LocalDateTime bookedAt = booking.getBookingDateTime();
            if (bookedAt.isBefore(rangeStart) || bookedAt.isAfter(rangeEnd)) {
                continue;
            }
            Show show = ownedShowsById.get(booking.getShowId());
            if (show == null) {
                continue; // Not one of this admin's shows.
            }

            Screen screen = ownedScreensById.get(show.getScreenId());
            Theatre theatre = theatreCache.computeIfAbsent(screen.getTheatreId(),
                    id -> theatreRepository.findById(id).orElse(null));
            Movie movie = movieCache.computeIfAbsent(show.getMovieId(),
                    id -> movieRepository.findById(id).orElse(null));

            String movieLabel = movie != null ? movie.getName() : ("Movie #" + show.getMovieId());
            String theatreLabel = theatre != null ? theatre.getName() : ("Theatre #" + screen.getTheatreId());

            report.byMovie
                    .computeIfAbsent(show.getMovieId(), k -> new RevenueLine(movieLabel))
                    .add(booking.getTotalAmount(), booking.getSeatIds().size());

            report.byTheatre
                    .computeIfAbsent(screen.getTheatreId(), k -> new RevenueLine(theatreLabel))
                    .add(booking.getTotalAmount(), booking.getSeatIds().size());

            report.totalRevenue += booking.getTotalAmount();
            report.totalBookings += 1;
            report.totalTicketsSold += booking.getSeatIds().size();
        }

        return report;
    }
}
