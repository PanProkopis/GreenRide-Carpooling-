package gr.hua.dit.greenride.dto;

import java.time.LocalDateTime;

public class RideResponse {

    private Long id;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private int availableSeats;
    private Long driverId;
    private String driverName;

    public RideResponse(Long id,
                        String origin,
                        String destination,
                        LocalDateTime departureTime,
                        int availableSeats,
                        Long driverId,
                        String driverName) {

        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.driverId = driverId;
        this.driverName = driverName;
    }

    public Long getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }
}