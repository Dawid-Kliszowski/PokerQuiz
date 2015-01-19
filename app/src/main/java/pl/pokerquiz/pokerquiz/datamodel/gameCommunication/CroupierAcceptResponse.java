package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

public class CroupierAcceptResponse {
    @SerializedName("accepted")
    private boolean mAccepted;

    public CroupierAcceptResponse(boolean accepted) {
        mAccepted = accepted;
    }

    public boolean isAccepted() {
        return mAccepted;
    }
}
