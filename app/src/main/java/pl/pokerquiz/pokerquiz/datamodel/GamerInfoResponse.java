package pl.pokerquiz.pokerquiz.datamodel;

import com.google.gson.annotations.SerializedName;

import pl.pokerquiz.pokerquiz.gameLogic.Gamer;

public class GamerInfoResponse {
    @SerializedName("accepted")
    private boolean mAccepted;

    @SerializedName("gamer")
    private Gamer mGamer;

    public GamerInfoResponse(boolean accepted, Gamer gamer) {
        mAccepted = accepted;
        mGamer = gamer;
    }

    public boolean isAccepted() {
        return mAccepted;
    }

    public Gamer getGamer() {
        return mGamer;
    }
}
