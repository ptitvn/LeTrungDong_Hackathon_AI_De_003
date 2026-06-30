package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class User {
    private String name;
    private int loyaltyPoints;

    public User(String name) {
        this.name = name;
        this.loyaltyPoints = 0;
    }

    public String getName() {
        return name;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addPoints(int points) {
        this.loyaltyPoints += points;
    }
}