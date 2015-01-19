package pl.pokerquiz.pokerquiz.networking;

import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Notification;

public interface GamerInteractingInterface {
    public void onGamersStateChanged(Gamer gamerMe, List<Gamer> gamers, int gamePhase);
    public void onNotificationRecived(Notification notification);
}
