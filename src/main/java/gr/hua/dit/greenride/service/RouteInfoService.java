package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.dto.RideRouteInfoResponse;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.external.GeocodedLocation;
import gr.hua.dit.greenride.external.GeocodingPort;
import gr.hua.dit.greenride.repository.RideRepository;
import org.springframework.stereotype.Service;

@Service
public class RouteInfoService {

    private final RideRepository rideRepository;
    private final GeocodingPort geocodingPort;

    public RouteInfoService(
            RideRepository rideRepository,
            GeocodingPort geocodingPort) {

        this.rideRepository = rideRepository;
        this.geocodingPort = geocodingPort;
    }

    public RideRouteInfoResponse getRouteInfo(
            Long rideId) {

        Ride ride = rideRepository
                .findById(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride not found"
                        ));

        GeocodedLocation origin =
                geocodingPort.geocode(
                        ride.getOrigin()
                );

        GeocodedLocation destination =
                geocodingPort.geocode(
                        ride.getDestination()
                );

        double distance =
                calculateDistance(
                        origin.getLatitude(),
                        origin.getLongitude(),
                        destination.getLatitude(),
                        destination.getLongitude()
                );

        double roundedDistance =
                Math.round(distance * 100.0)
                        / 100.0;

        return new RideRouteInfoResponse(
                ride.getId(),
                origin.getName(),
                origin.getLatitude(),
                origin.getLongitude(),
                destination.getName(),
                destination.getLatitude(),
                destination.getLongitude(),
                roundedDistance
        );
    }

    private double calculateDistance(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        final double earthRadiusKm = 6371.0;

        double latitudeDistance =
                Math.toRadians(
                        latitude2 - latitude1
                );

        double longitudeDistance =
                Math.toRadians(
                        longitude2 - longitude1
                );

        double a =
                Math.sin(latitudeDistance / 2)
                        * Math.sin(latitudeDistance / 2)

                        + Math.cos(
                        Math.toRadians(latitude1)
                )

                        * Math.cos(
                        Math.toRadians(latitude2)
                )

                        * Math.sin(
                        longitudeDistance / 2
                )

                        * Math.sin(
                        longitudeDistance / 2
                );

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadiusKm * c;
    }
}