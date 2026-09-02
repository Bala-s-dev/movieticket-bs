package com.movieticket.service;

import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Screen;
import com.movieticket.model.Seat;
import com.movieticket.model.Show;
import com.movieticket.model.TicketPricing;
import com.movieticket.repository.ShowRepository;
import com.movieticket.repository.ShowSeatRepository;
import com.movieticket.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ScreenService screenService;
    private final MovieService movieService;

    public ShowService(ShowRepository showRepository, ShowSeatRepository showSeatRepository,
            ScreenService screenService, MovieService movieService) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.screenService = screenService;
        this.movieService = movieService;
    }

    public Show addShow(long adminId, long theatreId, long screenId, long movieId,
            LocalDateTime start, LocalDateTime end, TicketPricing pricing) {
        Screen screen = screenService.getOwnedScreen(screenId, adminId);

        if (screen.getTheatreId() != theatreId) {
            throw new ValidationException("Selected screen does not belong to the selected theatre.");
        }

        movieService.getMovie(movieId);

        if (start == null || end == null) {
            throw new ValidationException("Show start and end time must be provided.");
        }
        if (!start.isBefore(end)) {
            throw new ValidationException("Show start time must be before end time.");
        }
        if (pricing == null) {
            throw new ValidationException("Pricing must be configured for the show.");
        }

        boolean overlaps = showRepository.findByScreenId(screenId).stream()
                .filter(Show::isActive)
                .anyMatch(existing -> existing.overlapsWith(start, end));
        if (overlaps) {
            throw new ValidationException("This screen already has an overlapping show in that time range.");
        }

        Show show = new Show(IdGenerator.nextShowId(), movieId, screenId, start, end, pricing);
        showRepository.save(show);

        List<Long> seatIds = screen.getAllSeats().stream().map(Seat::getSeatId).toList();
        showSeatRepository.initializeForShow(show.getShowId(), seatIds);

        return show;
    }

    public void removeShow(long showId, long adminId) {
        Show show = getShow(showId);
        screenService.getOwnedScreen(show.getScreenId(), adminId); // ownership check
        show.setActive(false);
        showRepository.save(show);
    }

    public Show getShow(long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + showId));
    }

    public List<Show> viewAllShows() {
        return showRepository.findAll();
    }

    public List<Show> viewUpcomingShows() {
        LocalDateTime now = LocalDateTime.now();
        return showRepository.findAll().stream()
                .filter(Show::isActive)
                .filter(s -> s.isUpcomingOrOngoing(now))
                .toList();
    }

    public List<Show> viewUpcomingShowsForMovie(long movieId) {
        LocalDateTime now = LocalDateTime.now();
        return showRepository.findByMovieId(movieId).stream()
                .filter(Show::isActive)
                .filter(s -> s.isUpcomingOrOngoing(now))
                .toList();
    }

    public List<Show> viewShowsForAdmin(long adminId) {
        return showRepository.findAll().stream()
                .filter(s -> {
                    try {
                        screenService.getOwnedScreen(s.getScreenId(), adminId);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }
}
