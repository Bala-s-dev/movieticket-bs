package com.movieticket.service;

import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.UnauthorizedAccessException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Screen;
import com.movieticket.model.Theatre;
import com.movieticket.repository.ScreenRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.repository.TheatreRepository;
import com.movieticket.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;

    public TheatreService(TheatreRepository theatreRepository, ScreenRepository screenRepository,
            ShowRepository showRepository) {

        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
        this.showRepository = showRepository;
    }

    public Theatre addTheatre(long adminId, String name, String location) {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Theatre name cannot be empty.");
        }

        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Theatre location cannot be empty.");
        }

        Theatre theatre = new Theatre(IdGenerator.nextTheatreId(), name, location, adminId);

        return theatreRepository.save(theatre);
    }

    public List<Theatre> viewMyTheatres(long adminId) {
        return theatreRepository.findByAdminId(adminId);
    }

    public Theatre getTheatre(long theatreId) {
        return theatreRepository
                .findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Theatre not found with ID: " + theatreId));
    }

    public Theatre getOwnedTheatre(long theatreId, long adminId) {

        Theatre theatre = getTheatre(theatreId);

        if (theatre.getAdminId() != adminId) {
            throw new UnauthorizedAccessException("You do not have permission to access this theatre.");
        }

        return theatre;
    }

    public void removeTheatre(long theatreId, long adminId) {

        Theatre theatre = getOwnedTheatre(theatreId, adminId);

        LocalDateTime currentDateTime = LocalDateTime.now();

        List<Screen> screens = screenRepository.findByTheatreId(theatreId);

        for (Screen screen : screens) {
            boolean hasFutureShow = showRepository
                    .findByScreenId(screen.getScreenId())
                    .stream()
                    .anyMatch(show -> show.isActive() && !show.getStartDateTime()
                            .isBefore(currentDateTime));

            if (hasFutureShow) {
                throw new ValidationException("Cannot remove theatre '" + theatre.getName()
                        + "': screen '" + screen.getScreenName()
                        + "' has active/future shows.");
            }
        }

        for (Screen screen : screens) {
            screen.setActive(false);
            screenRepository.save(screen);
        }

        theatre.setActive(false);
        theatreRepository.save(theatre);
    }
}
