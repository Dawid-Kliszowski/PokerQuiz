package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExchangeCardsRequest {
    @SerializedName("gamer_id")
    private String mGamerId;

    @SerializedName("cards_uuids")
    private List<String> mCardsUUIDs;

    public ExchangeCardsRequest(String gamerId, List<String> cardsUUIDs) {
        mGamerId = gamerId;
        mCardsUUIDs = cardsUUIDs;
    }

    public String getGamerId() {
        return mGamerId;
    }

    public List<String> getCardsUUIDs() {
        return mCardsUUIDs;
    }
}
