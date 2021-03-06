package pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol;

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
    private MessageType mMessageType;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("status")
    private int mStatus;

    public ResponsePacket(MessageType messageType, String message, int status, String requestChecksum) {
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

    public MessageType getMessageType() {
        return mMessageType;
    }

    public int getStatus() {
        return mStatus;
    }
}
