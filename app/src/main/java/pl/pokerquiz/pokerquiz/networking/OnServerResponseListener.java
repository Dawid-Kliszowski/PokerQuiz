package pl.pokerquiz.pokerquiz.networking;


import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol.MessageType;

public interface OnServerResponseListener {
    public void onServerResponse(boolean success, int serverStatus, MessageType messageType, String message);
}
