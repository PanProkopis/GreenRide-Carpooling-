package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.entity.Rating;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.BookingRepository;
import gr.hua.dit.greenride.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;

    public RatingService(
            RatingRepository ratingRepository,
            BookingRepository bookingRepository) {

        this.ratingRepository = ratingRepository;
        this.bookingRepository = bookingRepository;
    }

    public Rating createRating(
            int score,
            String comment,
            User fromUser,
            User toUser,
            Ride ride) {

        if (score < 1 || score > 5) {
            throw new IllegalArgumentException(
                    "Score must be between 1 and 5"
            );
        }

        if (fromUser == null) {
            throw new IllegalArgumentException(
                    "Rating must have a user who submits it"
            );
        }

        if (toUser == null) {
            throw new IllegalArgumentException(
                    "Rating must have a user who receives it"
            );
        }

        if (ride == null) {
            throw new IllegalArgumentException(
                    "Rating must belong to a ride"
            );
        }

        if (fromUser.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException(
                    "User cannot rate themselves"
            );
        }

        if (ride.getDepartureTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Ride has not happened yet"
            );
        }

        Long driverId = ride.getDriver().getId();

        boolean fromUserIsDriver =
                driverId.equals(fromUser.getId());

        boolean toUserIsDriver =
                driverId.equals(toUser.getId());

        boolean fromUserIsPassenger =
                bookingRepository.existsByPassengerIdAndRideId(
                        fromUser.getId(),
                        ride.getId()
                );

        boolean toUserIsPassenger =
                bookingRepository.existsByPassengerIdAndRideId(
                        toUser.getId(),
                        ride.getId()
                );

        boolean validRating =
                (fromUserIsDriver && toUserIsPassenger)
                        ||
                        (fromUserIsPassenger && toUserIsDriver);

        if (!validRating) {
            throw new IllegalArgumentException(
                    "Users must have participated in this ride as driver and passenger"
            );
        }

        if (ratingRepository.existsByFromUserIdAndToUserIdAndRideId(
                fromUser.getId(),
                toUser.getId(),
                ride.getId())) {

            throw new IllegalArgumentException(
                    "User has already rated this person for this ride"
            );
        }

        Rating rating = new Rating(
                score,
                comment,
                fromUser,
                toUser,
                ride
        );

        return ratingRepository.save(rating);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }
}