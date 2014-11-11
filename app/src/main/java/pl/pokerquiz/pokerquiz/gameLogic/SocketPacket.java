package pl.pokerquiz.pokerquiz.gameLogic;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

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

    public SocketPacket(String messageType, String message) throws JSONException {
        mMessageType = messageType;
        mMessage = message;
        mTimestamp = System.currentTimeMillis();
        mChecksum = "" + new Random().nextInt();
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
}
