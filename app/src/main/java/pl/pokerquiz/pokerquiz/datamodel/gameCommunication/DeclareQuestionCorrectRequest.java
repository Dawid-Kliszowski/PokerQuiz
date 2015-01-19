package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

public class DeclareQuestionCorrectRequest {
    @SerializedName("gamer_id")
    private String mGamerId;

    @SerializedName("cards_uuid")
    private String mCardUUID;


    public DeclareQuestionCorrectRequest(String gamerId, String cardUUID) {
        mGamerId = gamerId;
        mCardUUID = cardUUID;
    }

    public String getGamerId() {
        return mGamerId;
    }

    public String getCardUUID() {
        return mCardUUID;
    }
}
