package com.movieticket.service;

import com.movieticket.enums.BookingStatus;
import com.movieticket.exception.BookingException;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.SeatUnavailableException;
import com.movieticket.exception.UnauthorizedAccessException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Booking;
import com.movieticket.model.Screen;
import com.movieticket.model.Seat;
import com.movieticket.model.Show;
import com.movieticket.model.ShowSeat;
import com.movieticket.repository.BookingRepository;
import com.movieticket.repository.ShowSeatRepository;
import com.movieticket.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ShowService showService;
    private final ScreenService screenService;

    public BookingService(BookingRepository bookingRepository, ShowSeatRepository showSeatRepository,
                          ShowService showService, ScreenService screenService) {
        this.bookingRepository = bookingRepository;
        this.showSeatRepository = showSeatRepository;
        this.showService = showService;
        this.screenService = screenService;
    }

    public Booking bookSeats(long userId, long showId, List<String> seatLabels) {
        Show show = showService.getShow(showId);
        if (!show.isActive() || show.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new BookingException("This show is no longer available for booking.");
        }

        if (seatLabels == null || seatLabels.isEmpty()) {
            throw new ValidationException("At least one seat must be selected.");
        }

        Set<String> uniqueLabels = new LinkedHashSet<>();
        for (String label : seatLabels) {
            String normalized = label.trim().toUpperCase();
            if (!uniqueLabels.add(normalized)) {
                throw new ValidationException("Duplicate seat selected: " + normalized);
            }
        }

        Screen screen = screenService.getScreen(show.getScreenId());

        List<Seat> resolvedSeats = new ArrayList<>();
        for (String label : uniqueLabels) {
            Seat seat = findSeatByLabel(screen, label)
                    .orElseThrow(() -> new ValidationException("Seat " + label + " does not exist on this screen."));
            resolvedSeats.add(seat);
        }

        List<ShowSeat> showSeats = new ArrayList<>();
        for (Seat seat : resolvedSeats) {
            ShowSeat showSeat = showSeatRepository.findByShowIdAndSeatId(showId, seat.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seat state not initialized for seat " + seat.getLabel() + "."));
            if (!showSeat.isAvailable()) {
                throw new SeatUnavailableException("Seat " + seat.getLabel() + " is already booked.");
            }
            showSeats.add(showSeat);
        }

        double total = 0.0;
        for (Seat seat : resolvedSeats) {
            total += show.getPricing().getPrice(seat.getCategory());
        }

        for (ShowSeat showSeat : showSeats) {
            showSeat.markBooked();
            showSeatRepository.save(showSeat);
        }

        List<Long> seatIds = new ArrayList<>();
        for (Seat seat : resolvedSeats) seatIds.add(seat.getSeatId());

        Booking booking = new Booking(IdGenerator.nextBookingId(), userId, showId,
                LocalDateTime.now(), seatIds, total);
        return bookingRepository.save(booking);
    }

    public void cancelBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getUserId() != userId) {
            throw new UnauthorizedAccessException("You cannot cancel another user's booking.");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Only confirmed bookings can be cancelled.");
        }

        booking.cancel();
        bookingRepository.save(booking);

        for (Long seatId : booking.getSeatIds()) {
            showSeatRepository.findByShowIdAndSeatId(booking.getShowId(), seatId)
                    .ifPresent(showSeat -> {
                        showSeat.markAvailable();
                        showSeatRepository.save(showSeat);
                    });
        }
    }

    public List<Booking> getBookingHistory(long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<ShowSeat> getShowSeatStates(long showId) {
        return showSeatRepository.findByShowId(showId);
    }

    private java.util.Optional<Seat> findSeatByLabel(Screen screen, String label) {
        return screen.getAllSeats().stream()
                .filter(s -> s.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }
}
