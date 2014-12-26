package pl.pokerquiz.pokerquiz.networking;

import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class ResponsePacket {
    @SerializedName("request_checksum")
    private String mRequestChecksum;

    @SerializedName("checksum")
    private String mChecksum;

    @SerializedName("timestamp")
    private long mTimestamp;

    @SerializedName("message_type")
    private String mMessageType;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("status")
    private int mStatus;

    public ResponsePacket(String messageType, String message, int status, String requestChecksum) {
        mMessageType = messageType;
        mMessage = message;
        mTimestamp = System.currentTimeMillis();
        mChecksum = "" + new Random().nextInt();
        mStatus = status;
        mRequestChecksum = requestChecksum;
    }

    public String getRequestChecksum() {
        return mRequestChecksum;
    }

    public String getChecksum() {
        return mChecksum;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getMessageType() {
        return mMessageType;
    }

    public int getStatus() {
        return mStatus;
    }
}
