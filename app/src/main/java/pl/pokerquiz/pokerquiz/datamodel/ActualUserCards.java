package pl.pokerquiz.pokerquiz.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import pl.pokerquiz.pokerquiz.gameLogic.FullGameCard;

public class ActualUserCards {
    @SerializedName("user_cards")
    private List<FullGameCard> mUserCards;

    public ActualUserCards(List<FullGameCard> userCards) {
        mUserCards = userCards;
    }

    public List<FullGameCard> getUserCards() {
        return mUserCards;
    }
}
