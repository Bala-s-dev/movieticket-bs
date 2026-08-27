package com.movieticket.service;

import com.movieticket.model.TicketPricing;


public class PricingConfigService {

    public TicketPricing getDefaultPricing(double price) {
        return TicketPricing.defaultPricing(price);
    }
}
