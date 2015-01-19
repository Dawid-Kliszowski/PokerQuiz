package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.AppPrefs;
import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constants;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.AnswerSelfQuestionRequest;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.DeclareQuestionCorrectRequest;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.ExchangeCardsRequest;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GameState;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfoResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Notification;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol.MessageType;

public class ComunicationClientService extends CommunicationBasicService {
    private static final int INVALID_NUMBER = -1;

    private ClientServiceBinder mBinder;

    private GamerInteractingInterface mGamerInterface;

    public ComunicationClientService() {
        mBinder = new ClientServiceBinder();
    }

    private boolean mJoinedRoom;
    private GameState mGameState;
    private String mDeviceId;
    private List<Notification> mNotifications = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ClientServiceBinder extends Binder {
        public ComunicationClientService getService() {
            return ComunicationClientService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setClientSockets();
        mDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void registerGamerInterface(GamerInteractingInterface gamerInterface) {
        mGamerInterface = gamerInterface;
    }

    public void joinRoom(OnServerResponseListener listener) {
        AppPrefs prefs = ((PokerQuizApplication) getApplication()).getAppPrefs();
        GamerInfo info = new GamerInfo(mDeviceId, prefs.getNickname(), prefs.getAvatarBase64());

        sendMessage(Constants.SERVER_IP_ADDRESS, Constants.SERVER_MESSAGE_PORT_NUMBER, MessageType.gamer_info, GSON.toJson(info),
              null, true, Constants.DEFAULT_CROUPIER_DECISION_TIMEOUT, (success, serverStatus, messageType, message) -> {
                    if (success) {
                        GamerInfoResponse response = new Gson().fromJson(message, GamerInfoResponse.class);
                        if (response.isAccepted()) {
                            mJoinedRoom = true;
                        }
                    }
                    if (listener != null) {
                        listener.onServerResponse(success, serverStatus, messageType, message);
                    }
        });
    }

    public void exchangeCards(List<String> selectedCardsUUIDs, OnServerResponseListener listener) {
        ExchangeCardsRequest request = new ExchangeCardsRequest(mDeviceId, selectedCardsUUIDs);
        sendMessage(Constants.SERVER_IP_ADDRESS, Constants.SERVER_MESSAGE_PORT_NUMBER, MessageType.exchange_cards_request, GSON.toJson(request),
                null, true, Constants.DEFAULT_CROUPIER_DECISION_TIMEOUT, (success, serverStatus, messageType, message) -> {

                    if (listener != null) {
                        listener.onServerResponse(success, serverStatus, messageType, message);
                    }
                });
    }

    public void answerSelfQuestion(String cardUUID, int answer, OnServerResponseListener listener) {
        AnswerSelfQuestionRequest request = new AnswerSelfQuestionRequest(mDeviceId, cardUUID, answer);
        sendMessage(Constants.SERVER_IP_ADDRESS, Constants.SERVER_MESSAGE_PORT_NUMBER, MessageType.answer_self_question_request, GSON.toJson(request),
                null, true, Constants.DEFAULT_AUTOMATIC_DECISION_TIMEOUT, (success, serverStatus, messageType, message) -> {
                    if (listener != null) {
                        listener.onServerResponse(success, serverStatus, messageType, message);
                    }
                });
    }

    public void declareQuestionAsCorrect(String cardUUID, OnServerResponseListener listener) {
        DeclareQuestionCorrectRequest request = new DeclareQuestionCorrectRequest(mDeviceId, cardUUID);
        sendMessage(Constants.SERVER_IP_ADDRESS, Constants.SERVER_MESSAGE_PORT_NUMBER, MessageType.answer_self_question_request, GSON.toJson(request),
                null, true, Constants.DEFAULT_AUTOMATIC_DECISION_TIMEOUT, (success, serverStatus, messageType, message) -> {
                    if (listener != null) {
                        listener.onServerResponse(success, serverStatus, messageType, message);
                    }
                });
    }

    @Override
    protected void onPacketReceived(String ipAddres, MessageType messageType, String message, ResponseManager responseManager) {
        try {
            switch (messageType) {
                case actual_game_state:
                    mGameState = GSON.fromJson(message, GameState.class);
                    refreshGameState();
                    break;
                case notification:
                    Notification notification = GSON.fromJson(message, Notification.class);
                    mNotifications.add(notification);
                    if (mGamerInterface != null) {
                        mGamerInterface.onNotificationRecived(notification);
                    }
                    break;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeliveryFailure(String ipAddress) {

    }

    public void refreshGameState() {
        Gamer gamerMe = null;
        List<Gamer> otherGamers = new ArrayList<>();
        String myDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (mGameState != null) {
            for (Gamer gamer : mGameState.getGamers()) {
                if (gamer.getGamerId().equals(myDeviceId)) {
                    gamerMe = gamer;
                } else {
                    otherGamers.add(gamer);
                }
            }

            if (mGamerInterface != null) {
                mGamerInterface.onGamersStateChanged(gamerMe, otherGamers, mGameState.getGamePhase());
            }
        }
    }

    public boolean isJoinedRoom() {
        return mJoinedRoom;
    }
}
