package pl.pokerquiz.pokerquiz.networking;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;

import java.util.Random;

public class SocketPacket {
    @SerializedName("checksum")
    private String mChecksum;

    @SerializedName("timestamp")
    private long mTimestamp;

    @SerializedName("message_type")
    private String mMessageType;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("requires_response")
    private boolean mRequiresResponse;

    @SerializedName("response_timeout")
    private long mResponseTimeout;

    public SocketPacket(String messageType, String message) {
        mMessageType = messageType;
        mMessage = message;
        mTimestamp = System.currentTimeMillis();
        mChecksum = "" + new Random().nextInt();
        mRequiresResponse = false;
        mResponseTimeout = 0l;
    }

    public SocketPacket(String messageType, String message, long responseTimeout) throws JSONException {
        mMessageType = messageType;
        mMessage = message;
        mTimestamp = System.currentTimeMillis();
        mChecksum = "" + new Random().nextInt();
        mRequiresResponse = true;
        mResponseTimeout = responseTimeout;
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

    public boolean getRequiresResponse() {
        return mRequiresResponse;
    }

    public long getResponseTimeout() {
        return mResponseTimeout;
    }
}
