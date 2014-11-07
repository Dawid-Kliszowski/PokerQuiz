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

    @SerializedName("message")
    private JSONObject mMessage;

    public SocketPacket(Object message) throws JSONException {
        mMessage = new JSONObject(new Gson().toJson(message));
        mTimestamp = System.currentTimeMillis();
        mChecksum = "" + new Random().nextInt();
    }

    public String getChecksum() {
        return mChecksum;
    }

    public JSONObject getMessage() {
        return mMessage;
    }
}
