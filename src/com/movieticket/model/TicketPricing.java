package com.movieticket.model;

import com.movieticket.enums.SeatCategory;

import java.util.EnumMap;
import java.util.Map;

public class TicketPricing {

    private final Map<SeatCategory, Double> priceByCategory = new EnumMap<>(SeatCategory.class);

    public TicketPricing(double gold, double platinum, double silver) {
        priceByCategory.put(SeatCategory.GOLD, gold);
        priceByCategory.put(SeatCategory.PLATINUM, platinum);
        priceByCategory.put(SeatCategory.SILVER, silver);
    }

    public double getPrice(SeatCategory category) {
        return priceByCategory.getOrDefault(category, 0.0);
    }

    public static TicketPricing defaultPricing(double price) {
        return new TicketPricing(price, price, price);
    }

    @Override
    public String toString() {
        return "Pricing{GOLD=" + getPrice(SeatCategory.GOLD) +
                ", PLATINUM=" + getPrice(SeatCategory.PLATINUM) +
                ", SILVER=" + getPrice(SeatCategory.SILVER) + "}";
    }
}
