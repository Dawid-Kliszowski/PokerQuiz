package pl.pokerquiz.pokerquiz.datamodel;


import com.google.gson.annotations.SerializedName;

public class GamerInfo {
    @SerializedName("user_nick")
    private String mUserNick;

    @SerializedName("avatar_base64")
    private String mAvatarBase64;

    public GamerInfo(String userNick, String avatarBase64) {
        mUserNick = userNick;
        mAvatarBase64 = avatarBase64;
    }

    public String getNick() {
        return mUserNick;
    }

    public String getAvatarBase64() {
        return mAvatarBase64;
    }
}
