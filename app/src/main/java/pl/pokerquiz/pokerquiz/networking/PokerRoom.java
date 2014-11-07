package pl.pokerquiz.pokerquiz.networking;

public class PokerRoom {
    private String mRoomName;
    private String mNetworkName;
    private String mNetworkKey;

    public PokerRoom(String roomName, String networkName, String networkKey) {
        mRoomName = roomName;
        mNetworkName = networkName;
        mNetworkKey = networkKey;
    }

    public String getRoomName() {
        return mRoomName;
    }

    public String getNetworkName() {
        return mNetworkName;
    }

    public String getNetworkKey() {
        return mNetworkKey;
    }
}
