package gr.hua.dit.greenride.repository;

import gr.hua.dit.greenride.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByFromUserIdAndToUserIdAndRideId(
            Long fromUserId,
            Long toUserId,
            Long rideId
    );
}