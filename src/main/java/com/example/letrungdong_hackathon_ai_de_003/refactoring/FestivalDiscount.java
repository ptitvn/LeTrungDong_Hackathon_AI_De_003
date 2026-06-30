package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class FestivalDiscount implements DiscountStrategy {

    @Override
    public boolean supports(String discountCode) {
        return "FESTIVAL".equals(discountCode);
    }

    @Override
    public double apply(double totalAmount) {
        return totalAmount - 40000; // Giảm trực tiếp 40,000 VND
    }
}
