package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GameState {
    @SerializedName("gamers")
    private List<Gamer> mGamers;

    @SerializedName("game_phase")
    private int mGamePhase;

    public GameState(int gamePhase, List<Gamer> gamers) {
        mGamePhase = gamePhase;
        mGamers = gamers;
    }

    public List<Gamer> getGamers() {
        return mGamers;
    }

    public int getGamePhase() {
        return mGamePhase;
    }
}
