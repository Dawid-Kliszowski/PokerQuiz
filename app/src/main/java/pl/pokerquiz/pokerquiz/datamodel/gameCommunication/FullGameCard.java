package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;
import pl.pokerquiz.pokerquiz.gameLogic.PokerCard;

public class FullGameCard implements Serializable {
    @SerializedName("poker_card")
    private PokerCard mPokerCard;

    @SerializedName("question")
    private QuizQuestion mQuestion;

    @SerializedName("visible")
    private boolean mVisible;

    @SerializedName("active")
    private boolean mActive;

    @SerializedName("answered_correct")
    private boolean mAnsweredCorrect;

    @SerializedName("declared_correct")
    private boolean mDeclaredCorrect;

    public FullGameCard(PokerCard pokerCard, QuizQuestion question) {
        mPokerCard = pokerCard;
        mQuestion = question;
    }

    public PokerCard getPokerCard() {
        return mPokerCard;
    }

    public QuizQuestion getQuestion() {
        return mQuestion;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
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
}
