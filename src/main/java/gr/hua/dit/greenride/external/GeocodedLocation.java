package gr.hua.dit.greenride.external;

public class GeocodedLocation {

    private final String name;
    private final String country;
    private final double latitude;
    private final double longitude;

    public GeocodedLocation(
            String name,
            String country,
            double latitude,
            double longitude) {

        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}