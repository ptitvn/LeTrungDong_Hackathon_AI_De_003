package com.example.letrungdong_hackathon_ai_de_003.refactoring;

import com.example.letrungdong_hackathon_ai_de_003.refactoring.DiscountStrategy;
import com.example.letrungdong_hackathon_ai_de_003.refactoring.LoyaltyPointCalculator;
import com.example.letrungdong_hackathon_ai_de_003.refactoring.NotificationService;
import com.example.letrungdong_hackathon_ai_de_003.refactoring.SeatPricingStrategy;

import java.util.List;

public class TicketingService {

    private final List<SeatPricingStrategy> seatPricingStrategies;
    private final List<DiscountStrategy> discountStrategies;
    private final LoyaltyPointCalculator loyaltyPointCalculator;
    private final NotificationService notificationService;

    public TicketingService(List<SeatPricingStrategy> seatPricingStrategies,
                            List<DiscountStrategy> discountStrategies,
                            LoyaltyPointCalculator loyaltyPointCalculator,
                            NotificationService notificationService) {
        this.seatPricingStrategies = seatPricingStrategies;
        this.discountStrategies = discountStrategies;
        this.loyaltyPointCalculator = loyaltyPointCalculator;
        this.notificationService = notificationService;
    }

    public Ticket bookTicket(User user, ShowTime show, List<Seat> seats, String discountCode) {
        // 1. Tính tổng tiền dựa trên loại ghế
        double total = calculateSeatTotal(seats, show.getBasePrice());

        // 2. Áp dụng khuyến mãi
        total = applyDiscount(total, discountCode);

        // 3. Tích điểm thưởng
        int points = loyaltyPointCalculator.calculate(total);
        user.addPoints(points);

        // 4. Gửi thông báo
        notificationService.notifyBookingSuccess(user.getName(), show.getName());

        return new Ticket(user, show, seats, total);
    }

    private double calculateSeatTotal(List<Seat> seats, double basePrice) {
        double total = 0;
        for (Seat seat : seats) {
            total += seatPricingStrategies.stream()
                    .filter(strategy -> strategy.supports(seat.getType()))
                    .findFirst()
                    .map(strategy -> strategy.calculatePrice(basePrice))
                    .orElse(basePrice);
        }
        return total;
    }

    private double applyDiscount(double total, String discountCode) {
        if (discountCode == null) {
            return total;
        }
        return discountStrategies.stream()
                .filter(strategy -> strategy.supports(discountCode))
                .findFirst()
                .map(strategy -> strategy.apply(total))
                .orElse(total);
    }
}