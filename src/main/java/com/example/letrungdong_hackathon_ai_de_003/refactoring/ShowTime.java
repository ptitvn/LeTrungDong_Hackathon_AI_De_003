package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class ShowTime {
    private String name;
    private double basePrice;

    public ShowTime(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }
}