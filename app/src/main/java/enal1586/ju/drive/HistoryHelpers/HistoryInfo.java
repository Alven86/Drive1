package enal1586.ju.drive.HistoryHelpers;

public class HistoryInfo {
    private String mRideId;
    private String mTime;

    public HistoryInfo(String rideId, String time){
        this.mRideId = rideId;
        this.mTime = time;
    }

    public String getmRideId(){return mRideId;}
    public void setmRideId(String mRideId) {
        this.mRideId = mRideId;
    }

    public String getmTime(){return mTime;}
    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
