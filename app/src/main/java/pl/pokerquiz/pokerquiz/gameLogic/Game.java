package pl.pokerquiz.pokerquiz.gameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;

public class Game {
    private static final int MAX_GAMERS = 5;
    private boolean mReturnCardsToDeck;
    private List<PokerCard> mLeftCards;
    private List<QuizQuestion> mLeftQuestions;
    private List<Gamer> mGamers;

    public Game () {
        mLeftCards = Arrays.asList(PokerCard.values());
        shuffleLeftCards();
    }

    public void clearQuestions() {
        mLeftQuestions = new ArrayList<QuizQuestion>();
    }

    public void addQuestions(List<QuizQuestion> questions) {
        mLeftQuestions.addAll(0, questions);
    }

    public void shuffleQuestions() {
        Collections.shuffle(mLeftQuestions);
    }

    public void shuffleLeftCards() {
        Collections.shuffle(mLeftCards);
    }

    public void startNewRound() {
        mLeftCards = Arrays.asList(PokerCard.values());
        shuffleLeftCards();

        for (Gamer gamer : mGamers) {
            List<FullGameCard> cards = new ArrayList<FullGameCard>();
            for (int i = 0; i < 5; i++) {
                cards.add(getFullGamecard());
            }
            gamer.setCards(cards);
        }
    }

    private FullGameCard getFullGamecard() {
        PokerCard pokerCard = mLeftCards.get(mLeftCards.size() - 1);
        mLeftCards.remove(mLeftCards.size() - 1);
        QuizQuestion question = mLeftQuestions.get(mLeftQuestions.size() - 1);
        mLeftQuestions.remove(mLeftQuestions.size() - 1);

        return new FullGameCard(pokerCard, question);
    }

}
