package com.example.letrungdong_hackathon_ai_de_003.refactoring;

public class PushNotificationService implements NotificationService {

    @Override
    public void notifyBookingSuccess(String userName, String showName) {
        System.out.println("Push notification to " + userName + ": Ticket booked for " + showName + "!");
    }
}
