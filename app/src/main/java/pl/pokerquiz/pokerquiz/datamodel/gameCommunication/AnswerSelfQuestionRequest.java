package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

public class AnswerSelfQuestionRequest {
    @SerializedName("gamer_id")
    private String mGamerId;

    @SerializedName("cards_uuid")
    private String mCardUUID;

    @SerializedName("answer")
    private int mAnswer;

    public AnswerSelfQuestionRequest(String gamerId, String cardUUID, int answer) {
        mGamerId = gamerId;
        mCardUUID = cardUUID;
        mAnswer = answer;
    }

    public String getGamerId() {
        return mGamerId;
    }

    public String getCardUUID() {
        return mCardUUID;
    }

    public int getAnswer() {
        return mAnswer;
    }
}
