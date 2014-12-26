package pl.pokerquiz.pokerquiz.networking;

import java.util.List;

import pl.pokerquiz.pokerquiz.gameLogic.Gamer;

public interface GamerEventListener {
    public void onGamersRefresh(List<Gamer> gamers);
}
