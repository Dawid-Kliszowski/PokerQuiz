package pl.pokerquiz.pokerquiz.gameLogic;


public interface OnServerResponseListener {
    public void onServerResponse(boolean success, int serverStatus, String messageType, String message);
}
