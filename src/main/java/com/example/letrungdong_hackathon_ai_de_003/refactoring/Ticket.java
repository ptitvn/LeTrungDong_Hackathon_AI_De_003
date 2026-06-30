package com.example.letrungdong_hackathon_ai_de_003.refactoring;

import java.util.List;

public class Ticket {
    private User user;
    private ShowTime show;
    private List<Seat> seats;
    private double totalAmount;

    public Ticket(User user, ShowTime show, List<Seat> seats, double totalAmount) {
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.totalAmount = totalAmount;
    }

    public User getUser() {
        return user;
    }

    public ShowTime getShow() {
        return show;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}