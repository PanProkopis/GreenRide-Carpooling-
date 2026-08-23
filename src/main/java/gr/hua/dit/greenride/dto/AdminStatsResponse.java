package gr.hua.dit.greenride.dto;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalRides;
    private long totalBookings;
    private double averageOccupancyPercentage;

    public AdminStatsResponse(
            long totalUsers,
            long totalRides,
            long totalBookings,
            double averageOccupancyPercentage) {

        this.totalUsers = totalUsers;
        this.totalRides = totalRides;
        this.totalBookings = totalBookings;
        this.averageOccupancyPercentage =
                averageOccupancyPercentage;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public double getAverageOccupancyPercentage() {
        return averageOccupancyPercentage;
    }
}