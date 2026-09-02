package com.movieticket.service;

import com.movieticket.enums.SeatCategory;
import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Screen;
import com.movieticket.model.Seat;
import com.movieticket.model.Theatre;
import com.movieticket.repository.ScreenRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScreenService {

    public static class RowConfig {
        public final char row;
        public final SeatCategory category;
        public final int seatCount;

        public RowConfig(char row, SeatCategory category, int seatCount) {
            this.row = row;
            this.category = category;
            this.seatCount = seatCount;
        }
    }

    private final ScreenRepository screenRepository;
    private final TheatreService theatreService;
    private final ShowRepository showRepository;

    public ScreenService(ScreenRepository screenRepository, TheatreService theatreService,
                         ShowRepository showRepository) {
        this.screenRepository = screenRepository;
        this.theatreService = theatreService;
        this.showRepository = showRepository;
    }

    public Screen addScreen(long theatreId, long adminId, String screenName, List<RowConfig> rowConfigs) {
        Theatre theatre = theatreService.getOwnedTheatre(theatreId, adminId);

        if (screenName == null || screenName.trim().isEmpty()) {
            throw new ValidationException("Screen name cannot be empty.");
        }

        boolean duplicateName = screenRepository.findByTheatreId(theatreId).stream()
                .anyMatch(s -> s.isActive() && s.getScreenName().equalsIgnoreCase(screenName));
        if (duplicateName) {
            throw new ValidationException("A screen with this name already exists in theatre '" + theatre.getName() + "'.");
        }
        
        if (rowConfigs == null || rowConfigs.isEmpty()) {
            throw new ValidationException("At least one row must be configured.");
        }

        for (RowConfig rc : rowConfigs) {
            if (rc.seatCount <= 0) {
                throw new ValidationException("Every row must have at least one seat.");
            }
        }

        Screen screen = new Screen(IdGenerator.nextScreenId(), screenName, theatreId);

        for (RowConfig rc : rowConfigs) {
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= rc.seatCount; i++) {
                seats.add(new Seat(IdGenerator.nextSeatId(), screen.getScreenId(), rc.row, i, rc.category));
            }
            screen.addRow(rc.row, seats);
        }
        return screenRepository.save(screen);
    }

    public List<Screen> viewScreens(long theatreId, long adminId) {
        theatreService.getOwnedTheatre(theatreId, adminId);
        return screenRepository.findByTheatreId(theatreId);
    }

    public Screen getScreen(long screenId) {
        return screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with ID: " + screenId));
    }

    public Screen getScreen(long screenId, long adminId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with ID: " + screenId));
        theatreService.getOwnedTheatre(screen.getTheatreId(), adminId);
        return screen;
    }

    public Screen getOwnedScreen(long screenId, long adminId) {
        Screen screen = getScreen(screenId, adminId);
        theatreService.getOwnedTheatre(screen.getTheatreId(), adminId);
        return screen;
    }

    public void removeScreen(long screenId, long adminId) {
        Screen screen = getOwnedScreen(screenId, adminId);
        LocalDateTime now = LocalDateTime.now();
        boolean hasFutureShow = showRepository.findByScreenId(screenId).stream()
                .anyMatch(s -> s.isActive() && !s.getStartDateTime().isBefore(now));
        if (hasFutureShow) {
            throw new ValidationException(
                    "Cannot remove screen '" + screen.getScreenName() + "': it has active/future shows.");
        }
        screen.setActive(false);
        screenRepository.save(screen);
    }
}
