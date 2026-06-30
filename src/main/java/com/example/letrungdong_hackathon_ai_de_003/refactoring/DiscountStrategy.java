package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public interface DiscountStrategy {
    boolean supports(String discountCode);
    double apply(double totalAmount);
}
