package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class VipSeatPricing implements SeatPricingStrategy {

    @Override
    public boolean supports(String seatType) {
        return "VIP".equals(seatType);
    }

    @Override
    public double calculatePrice(double basePrice) {
        return basePrice + 20000;
    }
}
