package com.movieticket.controller;

import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Admin;
import com.movieticket.service.ReportService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.DateTimeUtil;
import com.movieticket.util.InputUtil;

import java.time.LocalDate;
import java.util.List;

public class ReportController {

    private final ReportService reportService;
    private final InputUtil input;

    public ReportController(ReportService reportService, InputUtil input) {
        this.reportService = reportService;
        this.input = input;
    }

    public void showRevenueReport(Admin admin) {
        try {
            ConsoleUtil.printHeader("REVENUE REPORT");
            LocalDate fromDate = input.readDate("From date");
            LocalDate toDate = input.readDate("To date");

            ReportService.RevenueReport report =
                    reportService.generateRevenueReport(admin.getAdminId(), fromDate, toDate);

            printReport(report);
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void printReport(ReportService.RevenueReport report) {
        System.out.println();
        System.out.println("Period: " + DateTimeUtil.formatDate(report.getFromDate()) +
                " to " + DateTimeUtil.formatDate(report.getToDate()));
        System.out.println();
        System.out.println("Total Revenue    : Rs." + report.getTotalRevenue());
        System.out.println("Total Bookings   : " + report.getTotalBookings());
        System.out.println("Total Tickets    : " + report.getTotalTicketsSold());

        List<ReportService.RevenueLine> byMovie = report.getByMovieSorted();
        System.out.println();
        System.out.println("Breakdown by Movie:");
        if (byMovie.isEmpty()) {
            System.out.println("  No confirmed bookings in this period.");
        } else {
            ConsoleUtil.printLine();
            System.out.printf("%-25s | %-12s | %-10s | %-10s%n", "Movie", "Revenue", "Tickets", "Bookings");
            ConsoleUtil.printLine();
            for (ReportService.RevenueLine line : byMovie) {
                System.out.printf("%-25s | Rs.%-9.1f | %-10d | %-10d%n",
                        line.getLabel(), line.getAmount(), line.getTicketsSold(), line.getBookingCount());
            }
            ConsoleUtil.printLine();
        }

        List<ReportService.RevenueLine> byTheatre = report.getByTheatreSorted();
        System.out.println();
        System.out.println("Breakdown by Theatre:");
        if (byTheatre.isEmpty()) {
            System.out.println("  No confirmed bookings in this period.");
        } else {
            ConsoleUtil.printLine();
            System.out.printf("%-25s | %-12s | %-10s | %-10s%n", "Theatre", "Revenue", "Tickets", "Bookings");
            ConsoleUtil.printLine();
            for (ReportService.RevenueLine line : byTheatre) {
                System.out.printf("%-25s | Rs.%-9.1f | %-10d | %-10d%n",
                        line.getLabel(), line.getAmount(), line.getTicketsSold(), line.getBookingCount());
            }
            ConsoleUtil.printLine();
        }
    }
}
