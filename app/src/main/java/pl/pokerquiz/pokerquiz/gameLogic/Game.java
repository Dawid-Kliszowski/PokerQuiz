package pl.pokerquiz.pokerquiz.gameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.database.DatabaseHelper;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GameState;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;

public class Game {
    private boolean mReturnCardsToDeck;
    private List<PokerCard> mLeftCards = new ArrayList<>();
    private List<QuizQuestion> mLeftQuestions = new ArrayList<>();
    private List<Gamer> mGamers = new ArrayList<>();
    private List<Category> mCategories = new ArrayList<>();
    private int mGamePhase;

    public Game (Collection<Gamer> gamers) {
        mGamers = new ArrayList<>();
        mGamers.addAll(gamers);
        for (Gamer gamer : mGamers) {
            gamer.setCards(new ArrayList<>());
            gamer.setCanExchangeCards(true);
        }
        mGamePhase = GamePhase.cards_exchanging.ordinal();
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
        fillQuestions();
    }

    private void fillQuestions() {
        mLeftQuestions = new ArrayList<>();

        DatabaseHelper helper = PokerQuizApplication.getDatabaseHelper();

        for (Category category : mCategories) {
            mLeftQuestions.addAll(helper.getByField(QuizQuestion.class, QuizQuestion.KEY_CATEGORY_ID, category.getId()));
        }
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
        fillQuestions();
        shuffleQuestions();
        mGamePhase = GamePhase.cards_exchanging.ordinal();

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

    public void exchangeCards(String gamerId, List<String> cardsUUIDs) {
        HashSet<String> cardIDsSet = new HashSet<>();
        for (String uuid : cardsUUIDs) {
            cardIDsSet.add(uuid);
        }

        for (Gamer gamer : mGamers) {
            if (gamer.getGamerId().equals(gamerId)) {
                gamer.setCanExchangeCards(false);
                int exchangedCards = 0;

                List<FullGameCard> gamerCards = new ArrayList<>();
                gamerCards.addAll(gamer.getCards());
                for (FullGameCard card : gamerCards) {
                    if (cardIDsSet.contains(card.getUUID())) {
                        exchangedCards++;
                        gamer.getCards().remove(card);
                    }
                }

                for (int i = 0; i < exchangedCards; i++) {
                    gamer.getCards().add(getFullGamecard());
                }
                break;
            }
        }
    }

    public FullGameCard findGameCard(String gamerId, String cardUUID) {
        for (Gamer gamer : mGamers) {
            if (gamer.getGamerId().equals(gamerId)) {
                for (FullGameCard card : gamer.getCards()) {
                    if (card.getUUID().equals(cardUUID)) {
                        return card;
                    }
                }
            }
        }
        return null;
    }

    public int getLeftCardsCount() {
        return mLeftCards.size();
    }

    public int getLeftQuestionsCount() {
        return mLeftQuestions.size();
    }

    public GameState getGameState() {
        return new GameState(mGamePhase, mGamers);
    }

    public List<Gamer> getGamers() {
        return mGamers;
    }

    public boolean startNextGamePhase() {
        if (mGamePhase < GamePhase.values().length - 1) {
            mGamePhase ++;
            return true;
        } else {
            return false;
        }
    }
}
