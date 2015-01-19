package pl.pokerquiz.pokerquiz.datamodel.gameCommunication;

import com.google.gson.annotations.SerializedName;

public class BasicMoveResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_NOT_ALLOWED = 3;

    @SerializedName("move_status")
    private int mMoveStatus;

    public BasicMoveResponse(int moveStatus) {
        mMoveStatus = moveStatus;
    }

    public int getMoveStatus() {
        return mMoveStatus;
    }
}
