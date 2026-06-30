package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class SweetboxSeatPricing implements SeatPricingStrategy {

    @Override
    public boolean supports(String seatType) {
        return "SWEETBOX".equals(seatType);
    }

    @Override
    public double calculatePrice(double basePrice) {
        return basePrice + 50000;
    }
}
