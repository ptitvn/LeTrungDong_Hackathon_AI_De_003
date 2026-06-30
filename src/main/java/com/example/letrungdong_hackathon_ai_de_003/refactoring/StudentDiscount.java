package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class StudentDiscount implements DiscountStrategy {

    @Override
    public boolean supports(String discountCode) {
        return "STUDENT".equals(discountCode);
    }

    @Override
    public double apply(double totalAmount) {
        return totalAmount * 0.9; // Giảm 10%
    }
}
