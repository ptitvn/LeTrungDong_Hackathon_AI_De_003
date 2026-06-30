package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class StandardSeatPricing implements SeatPricingStrategy {

    @Override
    public boolean supports(String seatType) {
        return "STANDARD".equals(seatType);
    }

    @Override
    public double calculatePrice(double basePrice) {
        return basePrice;
    }
}
