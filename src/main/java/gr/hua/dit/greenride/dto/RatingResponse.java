package gr.hua.dit.greenride.dto;

public class RatingResponse {

    private Long id;
    private int score;
    private String comment;

    private Long fromUserId;
    private String fromUserName;

    private Long toUserId;
    private String toUserName;

    private Long rideId;

    public RatingResponse(Long id,
                          int score,
                          String comment,
                          Long fromUserId,
                          String fromUserName,
                          Long toUserId,
                          String toUserName,
                          Long rideId) {

        this.id = id;
        this.score = score;
        this.comment = comment;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
        this.rideId = rideId;
    }

    public Long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public Long getRideId() {
        return rideId;
    }
}