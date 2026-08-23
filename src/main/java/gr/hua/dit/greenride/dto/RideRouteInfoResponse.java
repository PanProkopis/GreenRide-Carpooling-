package gr.hua.dit.greenride.dto;

public class RideRouteInfoResponse {

    private Long rideId;

    private String origin;
    private double originLatitude;
    private double originLongitude;

    private String destination;
    private double destinationLatitude;
    private double destinationLongitude;

    private double straightLineDistanceKm;

    public RideRouteInfoResponse(
            Long rideId,
            String origin,
            double originLatitude,
            double originLongitude,
            String destination,
            double destinationLatitude,
            double destinationLongitude,
            double straightLineDistanceKm) {

        this.rideId = rideId;
        this.origin = origin;
        this.originLatitude = originLatitude;
        this.originLongitude = originLongitude;
        this.destination = destination;
        this.destinationLatitude =
                destinationLatitude;
        this.destinationLongitude =
                destinationLongitude;
        this.straightLineDistanceKm =
                straightLineDistanceKm;
    }

    public Long getRideId() {
        return rideId;
    }

    public String getOrigin() {
        return origin;
    }

    public double getOriginLatitude() {
        return originLatitude;
    }

    public double getOriginLongitude() {
        return originLongitude;
    }

    public String getDestination() {
        return destination;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public double getStraightLineDistanceKm() {
        return straightLineDistanceKm;
    }
}