package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public interface SeatPricingStrategy {
    boolean supports(String seatType);
    double calculatePrice(double basePrice);
}
