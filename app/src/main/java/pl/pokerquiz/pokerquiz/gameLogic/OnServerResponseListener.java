package pl.pokerquiz.pokerquiz.gameLogic;

import org.json.JSONObject;

public interface OnServerResponseListener {
    public void onServerResponse(boolean success, int serverStatus);
}
