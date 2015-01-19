package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notification implements Serializable {
    @SerializedName("title")
    private String mTitle;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("time")
    private long mTime;

    public Notification(String title, String message, long time) {
        mTitle = title;
        mMessage = message;
        mTime = time;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public long getTime() {
        return mTime;
    }
}
