package pl.pokerquiz.pokerquiz.gameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.database.DatabaseHelper;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;

public class Game {
    private static final int MAX_GAMERS = 5;
    private boolean mReturnCardsToDeck;
    private List<PokerCard> mLeftCards = new ArrayList<>();
    private List<QuizQuestion> mLeftQuestions = new ArrayList<>();
    private List<Gamer> mGamers = new ArrayList<>();
    private List<Category> mCategories = new ArrayList<>();

    public Game (Collection<Gamer> gamers) {
        mGamers = new ArrayList<>();
        mGamers.addAll(gamers);
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
        mLeftQuestions = new ArrayList<>();

        DatabaseHelper helper = PokerQuizApplication.getDatabaseHelper();

        for (Category category : mCategories) {
            mLeftQuestions.addAll(helper.getByField(QuizQuestion.class, QuizQuestion.KEY_CATEGORY_ID, category.getId()));
        }
        Collections.shuffle(mLeftQuestions);
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    public void shuffleQuestions() {
        Collections.shuffle(mLeftQuestions);
    }

    public void shuffleLeftCards() {
        Collections.shuffle(mLeftCards);
    }

    public void startNewRound() {
        mLeftCards.clear();
        for (PokerCard card : PokerCard.values()) {
            mLeftCards.add(card);
        }
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

    public int getLeftCardsCount() {
        return mLeftCards.size();
    }

    public int getLeftQuestionsCount() {
        return mLeftQuestions.size();
    }

    public Collection<Gamer> getGamers() {
        return mGamers;
    }
}
