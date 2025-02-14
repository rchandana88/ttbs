public class Train {
    private int trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private int seatsAvailable;
    private String arrivalTime;
    private String arrivalDate;
    private int total_fare;
    private String bookingDate;
    private String email;

    public Train(int trainNumber, String trainName, String source, String destination, int seatsAvailable, String arrivalTime, String arrivalDate,int total_fare,String bookingDate,String email) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.seatsAvailable = seatsAvailable;
        this.arrivalTime = arrivalTime;
        this.arrivalDate = arrivalDate;
        this.bookingDate=bookingDate;
        this.total_fare=total_fare;
        this.email=email;

    }

    public int getTrainNumber() {
        return trainNumber;
    }
}
