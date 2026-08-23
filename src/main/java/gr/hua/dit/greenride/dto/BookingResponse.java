package gr.hua.dit.greenride.dto;

import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private LocalDateTime bookingTime;
    private Long passengerId;
    private String passengerName;
    private Long rideId;

    public BookingResponse(Long id,
                           LocalDateTime bookingTime,
                           Long passengerId,
                           String passengerName,
                           Long rideId) {

        this.id = id;
        this.bookingTime = bookingTime;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.rideId = rideId;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public Long getRideId() {
        return rideId;
    }
}