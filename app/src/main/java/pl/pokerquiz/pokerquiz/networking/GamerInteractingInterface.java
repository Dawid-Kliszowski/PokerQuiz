package pl.pokerquiz.pokerquiz.networking;

import java.util.List;

import pl.pokerquiz.pokerquiz.gameLogic.Gamer;

public interface GamerInteractingInterface {
    public void onGamersStateChanged(List<Gamer> gamers);
}
