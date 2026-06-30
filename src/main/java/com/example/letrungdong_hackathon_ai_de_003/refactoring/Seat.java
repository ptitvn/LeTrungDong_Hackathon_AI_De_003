package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class Seat {
    private String type; // VIP, SWEETBOX, NORMAL

    public Seat(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}