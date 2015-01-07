package pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol;

import com.google.gson.annotations.SerializedName;

public class ConfirmationPacket {
    @SerializedName("packet_checksum")
    private String mPacketChecksum;

    @SerializedName("confirmation_status")
    private int mConfirmationStatus;

    public ConfirmationPacket(String packetChecksum, int confirmationStatus) {
        mPacketChecksum = packetChecksum;
        mConfirmationStatus = confirmationStatus;
    }

    public String getPacketChecksum() {
        return mPacketChecksum;
    }

    public int getConfirmationStatus() {
        return mConfirmationStatus;
    }
}
