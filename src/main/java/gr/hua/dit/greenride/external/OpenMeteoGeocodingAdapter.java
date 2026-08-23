package gr.hua.dit.greenride.external;

import gr.hua.dit.greenride.exception.ExternalServiceException;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class OpenMeteoGeocodingAdapter
        implements GeocodingPort {

    private final RestClient restClient;

    public OpenMeteoGeocodingAdapter() {

        this.restClient = RestClient.builder()
                .baseUrl(
                        "https://geocoding-api.open-meteo.com"
                )
                .build();
    }

    @Override
    public GeocodedLocation geocode(
            String locationName) {

        try {

            GeocodingApiResponse response =
                    restClient
                            .get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path("/v1/search")
                                            .queryParam(
                                                    "name",
                                                    locationName
                                            )
                                            .queryParam(
                                                    "count",
                                                    1
                                            )
                                            .queryParam(
                                                    "language",
                                                    "en"
                                            )
                                            .queryParam(
                                                    "format",
                                                    "json"
                                            )
                                            .build()
                            )
                            .retrieve()
                            .body(
                                    GeocodingApiResponse.class
                            );

            if (response == null
                    || response.getResults() == null
                    || response.getResults().isEmpty()) {

                throw new ResourceNotFoundException(
                        "Location not found: "
                                + locationName
                );
            }

            GeocodingResult result =
                    response.getResults().getFirst();

            if (result.getLatitude() == null
                    || result.getLongitude() == null) {

                throw new ExternalServiceException(
                        "Geocoding service returned invalid coordinates"
                );
            }

            return new GeocodedLocation(
                    result.getName(),
                    result.getCountry(),
                    result.getLatitude(),
                    result.getLongitude()
            );

        } catch (ResourceNotFoundException ex) {

            throw ex;

        } catch (RestClientException ex) {

            throw new ExternalServiceException(
                    "Geocoding service is temporarily unavailable"
            );
        }
    }

    public static class GeocodingApiResponse {

        private List<GeocodingResult> results;

        public GeocodingApiResponse() {
        }

        public List<GeocodingResult> getResults() {
            return results;
        }

        public void setResults(
                List<GeocodingResult> results) {

            this.results = results;
        }
    }

    public static class GeocodingResult {

        private String name;
        private String country;
        private Double latitude;
        private Double longitude;

        public GeocodingResult() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}