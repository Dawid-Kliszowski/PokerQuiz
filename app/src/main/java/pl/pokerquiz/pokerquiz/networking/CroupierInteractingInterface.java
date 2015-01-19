package pl.pokerquiz.pokerquiz.networking;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;

public interface CroupierInteractingInterface {
    public void onGamerConnected(GamerInfo gamerInfo, AcceptingManager acceptManager);
    public void onCardsExchanging(String gamerNick, int numberOfCards, AcceptingManager acceptingManager);
}
