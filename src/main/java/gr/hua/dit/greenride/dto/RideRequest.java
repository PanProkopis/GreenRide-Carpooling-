package gr.hua.dit.greenride.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class RideRequest {

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    private LocalDateTime departureTime;

    @Min(
            value = 1,
            message = "Available seats must be at least 1"
    )
    @Max(
            value = 8,
            message = "Available seats cannot exceed 8"
    )
    private int availableSeats;

    public RideRequest() {
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(
            String origin) {

        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(
            String destination) {

        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(
            LocalDateTime departureTime) {

        this.departureTime = departureTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(
            int availableSeats) {

        this.availableSeats = availableSeats;
    }
}