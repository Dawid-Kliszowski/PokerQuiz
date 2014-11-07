package pl.pokerquiz.pokerquiz.gameLogic;

import java.util.ArrayList;
import java.util.List;

public class Gamer {
    private String mGamerName;
    private String mIpAddress;
    private List<FullGameCard> mCards;

    public Gamer(String gamerName, String ipAddress) {
        mGamerName = gamerName;
        mIpAddress = ipAddress;
        mCards = new ArrayList<FullGameCard>();
    }

    public void setCards(List<FullGameCard> cards) {
        mCards = cards;
    }
}
