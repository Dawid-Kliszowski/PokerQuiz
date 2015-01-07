package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.pokerquiz.pokerquiz.AppPrefs;
import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constants;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfoResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.gameLogic.OnServerResponseListener;

public class ComunicationClientService extends CommunicationBasicService {
    private static final int INVALID_NUMBER = -1;

    private ClientServiceBinder mBinder;

    private GamerInteractingInterface mGamerInterface;

    public ComunicationClientService() {
        mBinder = new ClientServiceBinder();
    }

    public List<Gamer> mGamers = new ArrayList<>();

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
    }

    public void registerGamerInterface(GamerInteractingInterface gamerInterface) {
        mGamerInterface = gamerInterface;
    }

    public void joinRoom(OnServerResponseListener listener) {
        AppPrefs prefs = ((PokerQuizApplication) getApplication()).getAppPrefs();
        String deviceId = getDeviceId();
        GamerInfo info = new GamerInfo(deviceId, prefs.getNickname(), prefs.getAvatarBase64());

        sendMessage(Constants.SERVER_IP_ADDRESS, Constants.SERVER_MESSAGE_PORT_NUMBER, MessageType.GAMER_INFO, GSON.toJson(info),
                null, true, Constants.ROOM_JOIN_ACCEPT_TIMEOUT, (success, serverStatus, messageType, message) -> {
            if (success) {
                GamerInfoResponse response = GSON.fromJson(message, GamerInfoResponse.class);
                if (response.isAccepted()) {
                    //todo
                }
            }
            listener.onServerResponse(success, serverStatus, messageType, message);

        });
    }

    @Override
    protected void onPacketReceived(String ipAddres, String messageType, String message, ResponseManager responseManager) {
        try {
            if (messageType.equals(MessageType.ACTUAL_GAMERS_STATE)) {
                mGamers = Arrays.asList(GSON.fromJson(message, Gamer[].class));

                refreshGameState();
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

    private String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void refreshGameState() {
        Gamer gamerMe = null;
        List<Gamer> otherGamers = new ArrayList<>();
        String myDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        for (Gamer gamer : mGamers) {
            if (gamer.getGamerId().equals(myDeviceId)) {
                gamerMe = gamer;
            } else {
                otherGamers.add(gamer);
            }
        }

        if (mGamerInterface != null) {
            mGamerInterface.onGamersStateChanged(gamerMe, otherGamers);
        }
    }
}
