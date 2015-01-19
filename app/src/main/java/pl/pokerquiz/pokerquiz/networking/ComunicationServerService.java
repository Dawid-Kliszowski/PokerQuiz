package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constants;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.AnswerSelfQuestionRequest;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.BasicMoveResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.CroupierAcceptResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.DeclareQuestionCorrectRequest;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.ExchangeCardsRequest;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfoResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Notification;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol.MessageType;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.gameLogic.Game;
import pl.pokerquiz.pokerquiz.utils.LocaleManager;

public class ComunicationServerService extends CommunicationBasicService {
    private ServerServiceBinder mBinder;

    private HashMap<String, Gamer> mGamerIdGamerMap = new HashMap<>();
    private HashMap<String, String> mGamerIdIpAddressMap = new HashMap<>();
    private HashMap<String, String> mIpAddressGamerIdMap = new HashMap<>();

    private CroupierInteractingInterface mCroupierInterface;
    private Game mGame = new Game(new ArrayList<>());

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

    public void sendBroadcastMessage(MessageType messageType, String message) {
        for (Gamer gamer : mGamerIdGamerMap.values()) {
            String ipAddress = mGamerIdIpAddressMap.get(gamer.getGamerId());
            sendMessage(ipAddress, Constants.CLIENT_MESSAGE_PORT_NUMBER, messageType, message, null, false, 0l, null);
        }
    }

    @Override
    public void onPacketReceived(String ipAddres, MessageType messageType, String message, ResponseManager responseManager) {
        try {
            switch (messageType) {
                case gamer_info:
                    GamerInfo gamerInfo = GSON.fromJson(message, GamerInfo.class);

                    if (mGamerIdIpAddressMap.containsKey(gamerInfo.getDeviceId())) {
                        mGamerIdIpAddressMap.put(gamerInfo.getDeviceId(), ipAddres);
                        if (mGamerIdGamerMap.containsKey(gamerInfo.getDeviceId())) {
                            responseManager.sendResponse(MessageType.gamer_info_response, GSON.toJson(new GamerInfoResponse(true, mGamerIdGamerMap.get(gamerInfo.getDeviceId()))), null);
                            broadcastActualGamersState();
                        }
                        return;
                    } else if (mGamerIdGamerMap.size() == Constants.MAXIMUM_NUMBER_OF_PLAYERS) {
                        responseManager.sendResponse(MessageType.gamer_info_response, GSON.toJson(new GamerInfoResponse(false, mGamerIdGamerMap.get(gamerInfo.getDeviceId()))), null);
                        return;
                    }

                    Gamer gamer = new Gamer(gamerInfo);

                    if (ipAddres.equals(Constants.SERVER_IP_ADDRESS)) {
                        String response = GSON.toJson(new GamerInfoResponse(true, gamer));
                        responseManager.sendResponse(MessageType.gamer_info_response, response, null);

                        mGamerIdGamerMap.put(gamer.getGamerId(), gamer);
                        mGamerIdIpAddressMap.put(gamer.getGamerId(), ipAddres);
                        mIpAddressGamerIdMap.put(ipAddres, gamer.getGamerId());

                        broadcastActualGamersState();

                    } else if (mCroupierInterface != null) {
                        AcceptingManager acceptManager = new AcceptingManager(new OnAcceptListener() {
                            @Override
                            public void onAccept(boolean accept) {
                                String response = GSON.toJson(new GamerInfoResponse(accept, gamer));
                                responseManager.sendResponse(MessageType.gamer_info_response, response, (success, status) -> {
                                    if (success && accept) {
                                        mGamerIdGamerMap.put(gamer.getGamerId(), gamer);
                                        mGamerIdIpAddressMap.put(gamer.getGamerId(), ipAddres);
                                        mIpAddressGamerIdMap.put(ipAddres, gamer.getGamerId());

                                        broadcastActualGamersState();
                                        Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();
                                        broadcastNotification(localizedRes.getString(R.string.new_gamer), gamer.getNickname() + " " + localizedRes.getString(R.string.has_joined_room));
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
                        responseManager.sendResponse(MessageType.gamer_info_response, response, null);
                    }
                    break;
                case exchange_cards_request:
                    ExchangeCardsRequest exchangeRequest = GSON.fromJson(message, ExchangeCardsRequest.class);
                    Gamer exchangeGamer = mGamerIdGamerMap.get(exchangeRequest.getGamerId());
                    if (exchangeGamer != null && exchangeGamer.getGamerId().equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))) {
                        String response = GSON.toJson(new CroupierAcceptResponse(true));
                        responseManager.sendResponse(MessageType.gamer_info_response, response, (success, status) -> {
                            if (success) {
                                mGame.exchangeCards(exchangeGamer.getGamerId(), exchangeRequest.getCardsUUIDs());

                                broadcastActualGamersState();
                                Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();
                                broadcastNotification(localizedRes.getString(R.string.exchanging_cards), exchangeGamer.getNickname() + " " + localizedRes.getString(R.string.exchanged_cards));
                            }
                        });
                    } else if (exchangeGamer != null && mCroupierInterface != null) {
                        AcceptingManager acceptManager = new AcceptingManager(new OnAcceptListener() {
                            @Override
                            public void onAccept(boolean accept) {
                                String response = GSON.toJson(new CroupierAcceptResponse(accept));
                                responseManager.sendResponse(MessageType.gamer_info_response, response, (success, status) -> {
                                    if (success && accept) {
                                        mGame.exchangeCards(exchangeGamer.getGamerId(), exchangeRequest.getCardsUUIDs());

                                        broadcastActualGamersState();
                                        Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();
                                        broadcastNotification(localizedRes.getString(R.string.exchanging_cards), exchangeGamer.getNickname() + " " + localizedRes.getString(R.string.exchanged_cards));
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
                        mCroupierInterface.onCardsExchanging(exchangeGamer.getNickname(), exchangeRequest.getCardsUUIDs().size(), acceptManager);
                    } else {
                        String response = GSON.toJson(new GamerInfoResponse(false, null));
                        responseManager.sendResponse(MessageType.gamer_info_response, response, null);
                    }

                    break;
                case answer_self_question_request:
                    AnswerSelfQuestionRequest request = GSON.fromJson(message, AnswerSelfQuestionRequest.class);
                    FullGameCard card = mGame.findGameCard(request.getGamerId(), request.getCardUUID());
                    if (card != null && !card.isAnsweredByOwner()) {
                        if (card.getQuestion().getCorrectAnswer() == request.getAnswer()) {
                            card.setAnsweredByOwner(true);
                            card.setAnsweredCorrect(true);
                            card.setDeclaredCorrect(true);
                            responseManager.sendResponse(MessageType.basic_move_response, GSON.toJson(new BasicMoveResponse(BasicMoveResponse.STATUS_SUCCESS)), null);
                        } else {
                            card.setAnsweredByOwner(true);
                            card.setAnsweredCorrect(false);
                            responseManager.sendResponse(MessageType.basic_move_response, GSON.toJson(new BasicMoveResponse(BasicMoveResponse.STATUS_FAILURE)), null);
                        }
                        broadcastActualGamersState();
                    } else {
                        responseManager.sendResponse(MessageType.basic_move_response, GSON.toJson(new BasicMoveResponse(BasicMoveResponse.STATUS_NOT_ALLOWED)), null);
                    }
                    break;
                case declare_question_as_correct_request:
                    DeclareQuestionCorrectRequest declareRequest = GSON.fromJson(message, DeclareQuestionCorrectRequest.class);
                    FullGameCard gameCard = mGame.findGameCard(declareRequest.getGamerId(), declareRequest.getCardUUID());
                    if (gameCard != null && !gameCard.isAnsweredByOwner()) {
                        gameCard.setDeclaredCorrect(true);
                        responseManager.sendResponse(MessageType.basic_move_response, GSON.toJson(new BasicMoveResponse(BasicMoveResponse.STATUS_SUCCESS)), null);
                        broadcastActualGamersState();
                    } else {
                        responseManager.sendResponse(MessageType.basic_move_response, GSON.toJson(new BasicMoveResponse(BasicMoveResponse.STATUS_NOT_ALLOWED)), null);
                    }
                    break;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastActualGamersState() {
        sendBroadcastMessage(MessageType.actual_game_state, GSON.toJson(mGame.getGameState()));
    }

    private void broadcastNotification(String title, String message) {
        Notification notification = new Notification(title, message, System.currentTimeMillis());
        sendBroadcastMessage(MessageType.notification, GSON.toJson(notification));
    }

    public void startNewGame() {
        mGame = new Game(mGamerIdGamerMap.values());
        broadcastActualGamersState();
        Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();
        broadcastNotification(localizedRes.getString(R.string.new_game_started), localizedRes.getString(R.string.croupier_started_game) + " " + mGame.getGamers().size());
    }

    public void startNewRound() {
        mGame.startNewRound();
        broadcastActualGamersState();
        Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();
        broadcastNotification(localizedRes.getString(R.string.new_round_started), "");
    }

    public void startNextGamePhase() {
        if (mGame.startNextGamePhase()) {
            broadcastActualGamersState();
            Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();
            broadcastNotification(localizedRes.getString(R.string.next_phase_started), "");
        }
    }

    public void setCategories(List<Category> categories) {
        mGame.setCategories(categories);
        Resources localizedRes = LocaleManager.getInstance(ComunicationServerService.this).getLocalizedResources();

        String message = localizedRes.getString(R.string.croupier_selected_categories);
        for (Category category : categories) {
            message += "\n" + category.getName();
        }
        broadcastNotification(localizedRes.getString(R.string.categories_selected), message);
    }

    public List<Category> getCategories() {
        return mGame.getCategories();
    }

    @Override
    public void onDeliveryFailure(String ipAddress) {

    }
}
