package gr.hua.dit.greenride.external;

public interface GeocodingPort {

    GeocodedLocation geocode(String locationName);
}
