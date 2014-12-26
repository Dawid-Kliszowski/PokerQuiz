package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pl.pokerquiz.pokerquiz.AppPrefs;
import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constans;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.datamodel.GamerInfo;
import pl.pokerquiz.pokerquiz.datamodel.GamerInfoResponse;
import pl.pokerquiz.pokerquiz.gameLogic.Game;
import pl.pokerquiz.pokerquiz.gameLogic.Gamer;
import pl.pokerquiz.pokerquiz.gameLogic.OnServerResponseListener;

public class ComunicationClientService extends CommunicationBasicService {
    private static final int INVALID_NUMBER = -1;

    private ClientServiceBinder mBinder;

    private Gamer mGamerMe;
    private GamerInteractingInterface mGamerInterface;

    public ComunicationClientService() {
        mBinder = new ClientServiceBinder();
    }

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
        GamerInfo info = new GamerInfo(prefs.getNickname(), prefs.getAvatarBase64());

        sendMessage(Constans.SERVER_IP_ADDRESS, Constans.SERVER_MESSAGE_PORT_NUMBER, MessageType.GAMER_INFO, GSON.toJson(info),
                null, true, Constans.ROOM_JOIN_ACCEPT_TIMEOUT, (success, serverStatus, messageType, message) -> {
            if (success) {
                GamerInfoResponse response = GSON.fromJson(message, GamerInfoResponse.class);
                if (response.isAccepted()) {
                    mGamerMe = response.getGamer();
                }
            }
            listener.onServerResponse(success, serverStatus, messageType, message);

        });
    }

    @Override
    protected void onPacketReceived(String ipAddres, String messageType, String message, ResponseManager responseManager) {
        try {
            if (messageType.equals(MessageType.ACTUAL_GAMERS_STATE)) {
                Gamer[] gamers = GSON.fromJson(message, Gamer[].class);
                if (mGamerInterface != null) {
                    mGamerInterface.onGamersStateChanged(Arrays.asList(gamers));
                }
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
}
