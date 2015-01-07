package pl.pokerquiz.pokerquiz.networking;

import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;

public interface GamerInteractingInterface {
    public void onGamersStateChanged(Gamer gamerMe, List<Gamer> gamers);
}
