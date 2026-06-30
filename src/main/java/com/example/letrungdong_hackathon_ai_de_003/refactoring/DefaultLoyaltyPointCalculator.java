package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class DefaultLoyaltyPointCalculator implements LoyaltyPointCalculator {

    private static final double POINTS_PER_UNIT = 10000;

    @Override
    public int calculate(double totalAmount) {
        return (int) (totalAmount / POINTS_PER_UNIT);
    }
}
