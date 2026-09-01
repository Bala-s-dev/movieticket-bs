package com.movieticket;

import com.movieticket.controller.*;
import com.movieticket.repository.*;
import com.movieticket.repository.DAO.DAOUserRepository;
import com.movieticket.repository.DAO.DAOAdminRepository;
import com.movieticket.repository.DAO.DAOMovieRepository;
import com.movieticket.repository.DAO.DAOTheatreRepository;
import com.movieticket.repository.DAO.DAOScreenRepository;
import com.movieticket.repository.DAO.DAOShowRepository;
import com.movieticket.repository.DAO.DAOShowSeatRepository;
import com.movieticket.repository.DAO.DAOBookingRepository;


// import com.movieticket.repository.local.*;
import com.movieticket.service.*;
import com.movieticket.util.InputUtil;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InputUtil input = new InputUtil(scanner);

        // UserRepository userRepository = new LocalUserRepository();
        // AdminRepository adminRepository = new LocalAdminRepository();
        // MovieRepository movieRepository = new LocalMovieRepository();
        // TheatreRepository theatreRepository = new LocalTheatreRepository();
        // ScreenRepository screenRepository = new LocalScreenRepository();
        // ShowRepository showRepository = new LocalShowRepository();
        // BookingRepository bookingRepository = new LocalBookingRepository();
        // ShowSeatRepository showSeatRepository = new LocalShowSeatRepository();

        UserRepository userRepository = new DAOUserRepository();
        AdminRepository adminRepository = new DAOAdminRepository();
        MovieRepository movieRepository = new DAOMovieRepository();
        TheatreRepository theatreRepository = new DAOTheatreRepository();
        ScreenRepository screenRepository = new DAOScreenRepository();
        ShowRepository showRepository = new DAOShowRepository();
        BookingRepository bookingRepository = new DAOBookingRepository();
        ShowSeatRepository showSeatRepository = new DAOShowSeatRepository();

        AuthService authService = new AuthService(userRepository, adminRepository);
        PricingConfigService pricingConfigService = new PricingConfigService();
        MovieService movieService = new MovieService(movieRepository, showRepository);
        TheatreService theatreService = new TheatreService(theatreRepository, screenRepository, showRepository);
        ScreenService screenService = new ScreenService(screenRepository, theatreService, showRepository);
        ShowService showService = new ShowService(showRepository, showSeatRepository, screenService, movieService);
        BookingService bookingService = new BookingService(bookingRepository, showSeatRepository, showService,
                screenService);
        ReportService reportService = new ReportService(bookingRepository, showRepository, screenRepository,
                theatreRepository, movieRepository);

        MovieController movieController = new MovieController(movieService, input);
        TheatreController theatreController = new TheatreController(theatreService, input);
        ScreenController screenController = new ScreenController(screenService, input);
        ShowController showController = new ShowController(showService, movieService, pricingConfigService, input);
        ReportController reportController = new ReportController(reportService, input);
        AdminController adminController = new AdminController(authService, movieController, theatreController,
                screenController, showController, reportController, input);
        UserController userController = new UserController(authService, movieService, showService, screenService,
                bookingService, theatreService, input);

        MainMenuController mainMenuController = new MainMenuController(userController, adminController, input);

        mainMenuController.run();
        scanner.close();
    }
}
