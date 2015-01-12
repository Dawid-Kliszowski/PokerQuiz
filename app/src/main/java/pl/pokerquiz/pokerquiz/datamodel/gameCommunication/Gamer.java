package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;

public class Gamer implements Serializable {
    @SerializedName("device_id")
    private String mDeviceId;

    @SerializedName("nickname")
    private String mNickname;

    @SerializedName("avatar_base64")
    private String mAvatarBase64;

    @SerializedName("cards")
    private List<FullGameCard> mCards;

    public Gamer(GamerInfo gamerInfo) {
        mDeviceId = gamerInfo.getDeviceId();
        mNickname = gamerInfo.getNick();
        mAvatarBase64 = gamerInfo.getAvatarBase64();
        mCards = new ArrayList<FullGameCard>();
    }

    public void setCards(List<FullGameCard> cards) {
        mCards = cards;
    }

    public List<FullGameCard> getCards() {
        return mCards;
    }

    public String getGamerId() {
        return mDeviceId;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getAvatarBase64() {
        return mAvatarBase64;
    }
}
