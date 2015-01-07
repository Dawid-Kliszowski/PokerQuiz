package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;


import com.google.gson.annotations.SerializedName;

public class GamerInfo {
    @SerializedName("device_id")
    private String mDeviceId;

    @SerializedName("user_nick")
    private String mUserNick;

    @SerializedName("avatar_base64")
    private String mAvatarBase64;

    public GamerInfo(String deviceId, String userNick, String avatarBase64) {
        mDeviceId = deviceId;
        mUserNick = userNick;
        mAvatarBase64 = avatarBase64;
    }

    public String getNick() {
        return mUserNick;
    }

    public String getAvatarBase64() {
        return mAvatarBase64;
    }

    public String getDeviceId() {
        return mDeviceId;
    }
}
