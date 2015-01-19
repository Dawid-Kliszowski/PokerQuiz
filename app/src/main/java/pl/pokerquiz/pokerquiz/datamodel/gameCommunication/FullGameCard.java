package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;
import pl.pokerquiz.pokerquiz.gameLogic.PokerCard;

public class FullGameCard implements Serializable {
    @SerializedName("uuid")
    private String mUUID;

    @SerializedName("poker_card")
    private PokerCard mPokerCard;

    @SerializedName("question")
    private QuizQuestion mQuestion;

    @SerializedName("active")
    private boolean mActive;

    @SerializedName("is_answered_by_owner")
    private boolean mIsAnsweredByOwner;

    @SerializedName("answered_correct")
    private boolean mAnsweredCorrect;

    @SerializedName("declared_correct")
    private boolean mDeclaredCorrect;

    public FullGameCard(PokerCard pokerCard, QuizQuestion question) {
        mPokerCard = pokerCard;
        mQuestion = question;
        mUUID = UUID.randomUUID().toString();
        mActive = true;
    }

    public String getUUID() {
        return mUUID;
    }

    public PokerCard getPokerCard() {
        return mPokerCard;
    }

    public QuizQuestion getQuestion() {
        return mQuestion;
    }

    public boolean isAnsweredByOwner() {
        return mIsAnsweredByOwner;
    }

    public void setAnsweredByOwner(boolean answered) {
        mIsAnsweredByOwner = answered;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public boolean isAnsweredCorrect() {
        return mAnsweredCorrect;
    }

    public void setAnsweredCorrect(boolean correct) {
        mAnsweredCorrect = correct;
    }

    public boolean isDeclaredCorrect() {
        return mDeclaredCorrect;
    }

    public void setDeclaredCorrect(boolean correct) {
        mDeclaredCorrect = correct;
    }
}
