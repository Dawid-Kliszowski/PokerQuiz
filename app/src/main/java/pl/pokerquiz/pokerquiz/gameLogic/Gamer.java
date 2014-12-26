package pl.pokerquiz.pokerquiz.gameLogic;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.GamerInfo;

public class Gamer {
    @SerializedName("gamer_id")
    private long mGamerId;

    @SerializedName("nickname")
    private String mNickname;

    @SerializedName("avatar_base64")
    private String mAvatarBase64;

    @SerializedName("cards")
    private List<FullGameCard> mCards;

    public Gamer(long gamerId, GamerInfo gamerInfo) {
        mGamerId = gamerId;
        mNickname = gamerInfo.getNick();
        mAvatarBase64 = gamerInfo.getAvatarBase64();
        mCards = new ArrayList<FullGameCard>();
    }

    public void setCards(List<FullGameCard> cards) {
        mCards = cards;
    }

    public long getGamerId() {
        return mGamerId;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getAvatarBase64() {
        return mAvatarBase64;
    }
}
