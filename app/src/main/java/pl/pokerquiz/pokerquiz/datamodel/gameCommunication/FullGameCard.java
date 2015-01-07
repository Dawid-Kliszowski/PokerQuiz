package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;
import pl.pokerquiz.pokerquiz.gameLogic.PokerCard;

public class FullGameCard {
    @SerializedName("poker_card")
    private PokerCard mPokerCard;

    @SerializedName("question")
    private QuizQuestion mQuestion;

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
}
