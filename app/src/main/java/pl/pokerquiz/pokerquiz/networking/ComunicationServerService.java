package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constants;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfoResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.gameLogic.PokerCard;

public class ComunicationServerService extends CommunicationBasicService {
    private ServerServiceBinder mBinder;

    private HashMap<String, Gamer> mGamerIdGamerMap = new HashMap<>();
    private HashMap<String, String> mGamerIdIpAddressMap = new HashMap<>();
    private HashMap<String, String> mIpAddressGamerIdMap = new HashMap<>();

    private CroupierInteractingInterface mCroupierInterface;

    public ComunicationServerService() {
        mBinder = new ServerServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServerServiceBinder extends Binder {
        public ComunicationServerService getService() {
            return ComunicationServerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setServerSockets();
    }

    public void registerCroupierInterface(CroupierInteractingInterface croupierInterface) {
        mCroupierInterface = croupierInterface;
    }

    public void unregisterCroupierInterface() {
        mCroupierInterface = null;
    }

    public void sendBroadcastMessage(String messageType, String message) {
        for (Gamer gamer : mGamerIdGamerMap.values()) {
            String ipAddress = mGamerIdIpAddressMap.get(gamer.getGamerId());
            if (ipAddress != null) {
                sendMessage(ipAddress, Constants.CLIENT_MESSAGE_PORT_NUMBER, messageType, message, null, false, 0l, null);
            } else {
                //todo
            }
        }
    }

    @Override
    public void onPacketReceived(String ipAddres, String messageType, String message, ResponseManager responseManager) {
        try {
            if (messageType.equals(MessageType.GAMER_INFO)) {
                GamerInfo gamerInfo = GSON.fromJson(message, GamerInfo.class);

                if (mGamerIdIpAddressMap.containsKey(gamerInfo.getDeviceId())) {
                    mGamerIdIpAddressMap.put(gamerInfo.getDeviceId(), ipAddres);
                    return;
                }

                Gamer gamer = new Gamer(gamerInfo);

                if (ipAddres.equals(Constants.SERVER_IP_ADDRESS)) {
                    String response = GSON.toJson(new GamerInfoResponse(true, gamer));
                    responseManager.sendResponse(MessageType.GAMER_INFO_RESPONSE, response, null);

                    mGamerIdGamerMap.put(gamer.getGamerId(), gamer);
                    mGamerIdIpAddressMap.put(gamer.getGamerId(), ipAddres);
                    mIpAddressGamerIdMap.put(ipAddres, gamer.getGamerId());

                    broadcastActualGamersState();

                } else if (mCroupierInterface != null) {
                    AcceptingManager acceptManager = new AcceptingManager(new OnAcceptListener() {
                        @Override
                        public void onAccept(boolean accept) {
                            String response = GSON.toJson(new GamerInfoResponse(accept, gamer));
                            responseManager.sendResponse(MessageType.GAMER_INFO_RESPONSE, response, (success, status) -> {
                                if (success && accept) {
                                    mGamerIdGamerMap.put(gamer.getGamerId(), gamer);
                                    mGamerIdIpAddressMap.put(gamer.getGamerId(), ipAddres);
                                    mIpAddressGamerIdMap.put(ipAddres, gamer.getGamerId());

                                    broadcastActualGamersState();
                                }
                            });
                        }
                    });
                    responseManager.setTimeoutListener(new OnTimeoutListener() {
                        @Override
                        public void onTimeout() {
                            acceptManager.onTimeout();
                        }
                    });
                    mCroupierInterface.onGamerConnected(gamerInfo, acceptManager);
                } else {
                    String response = GSON.toJson(new GamerInfoResponse(false, null));
                    responseManager.sendResponse(MessageType.GAMER_INFO_RESPONSE, response, null);
                }

            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastActualGamersState() {
        sendBroadcastMessage(MessageType.ACTUAL_GAMERS_STATE, GSON.toJson(mGamerIdGamerMap.values()));
    }

    public void dealCards() {
        List<PokerCard> allCards = new ArrayList<>(Arrays.asList(PokerCard.values()));
        Collections.shuffle(allCards);
        for (Gamer gamer : mGamerIdGamerMap.values()) {
            List<FullGameCard> gamerCards = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                gamerCards.add(new FullGameCard(allCards.get(allCards.size() - 1), null));
                allCards.remove(allCards.size() - 1);
            }

            gamer.setCards(gamerCards);
            broadcastActualGamersState();
        }
    }

    @Override
    public void onDeliveryFailure(String ipAddress) {

    }
}
