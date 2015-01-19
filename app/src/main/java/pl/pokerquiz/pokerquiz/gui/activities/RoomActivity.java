package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.BasicMoveResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.CroupierAcceptResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GameState;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfoResponse;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Notification;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol.MessageType;
import pl.pokerquiz.pokerquiz.gameLogic.GamePhase;
import pl.pokerquiz.pokerquiz.gui.dialogs.TopNotificationDialog;
import pl.pokerquiz.pokerquiz.networking.OnServerResponseListener;
import pl.pokerquiz.pokerquiz.gui.dialogs.BigCardDialogFragment;
import pl.pokerquiz.pokerquiz.gui.fragments.CroupierMenuFragment;
import pl.pokerquiz.pokerquiz.gui.fragments.MainMenuFragment;
import pl.pokerquiz.pokerquiz.gui.views.CardsView;
import pl.pokerquiz.pokerquiz.networking.ComunicationClientService;
import pl.pokerquiz.pokerquiz.networking.GamerInteractingInterface;
import pl.pokerquiz.pokerquiz.gui.views.SelfCardsView;
import pl.pokerquiz.pokerquiz.networking.ServerStatusConstans;

public class RoomActivity extends Activity implements GamerInteractingInterface {
    private ComunicationClientService mClientService;

    private FrameLayout mFlFragmentContainer;
    private ImageView mImgvMenuButton;
    private ImageView mImgvMenuButtonRight;
    private ImageView mImgvBottomArrow;

    private SlidingMenu mSlidingMenu;
    private List<LinearLayout> mPlayerHolders;
    private CardsView mCardsView;
    private SelfCardsView mSelfCardsView;
    private Button mBtnExchangeCards;

    private DisplayImageOptions mAvatarOptions;
    private boolean mIsCroupier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mIsCroupier = PokerQuizApplication.getServerService() != null;

        findViews();
        setListeners();

        initGamerMenu();

        mAvatarOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(1000, 0))
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        if (mClientService == null) {
            initClientService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mClientService != null) {
            mClientService.refreshGameState();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            mFlFragmentContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void setFragment(Fragment fragment, boolean clearBackStack) {
        mFlFragmentContainer.setVisibility(View.VISIBLE);
        if (clearBackStack) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragmentContainer, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
        mSlidingMenu.showContent();
    }

    private void findViews() {
        mFlFragmentContainer = (FrameLayout) findViewById(R.id.flFragmentContainer);
        mImgvMenuButton = (ImageView) findViewById(R.id.imgvMenuButton);
        mImgvMenuButtonRight = (ImageView) findViewById(R.id.imgvMenuButtonRight);
        mImgvBottomArrow = (ImageView) findViewById(R.id.imgvBottomArrow);
        mBtnExchangeCards = (Button) findViewById(R.id.btnExchangeCards);

        mPlayerHolders = new ArrayList<>();
        mPlayerHolders.add((LinearLayout) findViewById(R.id.llplayerHolderFirst));
        mPlayerHolders.add((LinearLayout) findViewById(R.id.llplayerHolderSecond));
        mPlayerHolders.add((LinearLayout) findViewById(R.id.llplayerHolderThird));
        mPlayerHolders.add((LinearLayout) findViewById(R.id.llplayerHolderFourth));
        mPlayerHolders.add((LinearLayout) findViewById(R.id.llplayerHolderFifth));

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (LinearLayout playerCardHolder : mPlayerHolders) {
            playerCardHolder.addView(inflater.inflate(R.layout.gamer_card, playerCardHolder, false));

            GamerCardViewHolder viewHolder = new GamerCardViewHolder();
            viewHolder.mImgvAvatar = (ImageView) playerCardHolder.findViewById(R.id.imgvAvatar);
            viewHolder.mTxtvNickname = (TextView) playerCardHolder.findViewById(R.id.txtvNickname);

            List<ViewGroup> microCardViews = new ArrayList<>();
            microCardViews.add((ViewGroup) playerCardHolder.findViewById(R.id.cardHolderFirst));
            microCardViews.add((ViewGroup) playerCardHolder.findViewById(R.id.cardHolderSecond));
            microCardViews.add((ViewGroup) playerCardHolder.findViewById(R.id.cardHolderThird));
            microCardViews.add((ViewGroup) playerCardHolder.findViewById(R.id.cardHolderFourth));
            microCardViews.add((ViewGroup) playerCardHolder.findViewById(R.id.cardHolderFifth));

            viewHolder.mMicroCardHolders = new ArrayList<>();

            for (ViewGroup microCardView : microCardViews) {
                microCardView.addView(inflater.inflate(R.layout.micro_game_card, microCardView, false));

                MicroGameCardViewHolder microCardHolder = new MicroGameCardViewHolder();
                microCardHolder.mLlContentHolder = (LinearLayout) microCardView.findViewById(R.id.llContentHolder);
                microCardHolder.mImgvPoint = (ImageView) microCardView.findViewById(R.id.imgvPoint);
                microCardHolder.mImgvCardColor = (ImageView) microCardView.findViewById(R.id.imgvCardColor);
                microCardHolder.mTxtvFigure = (TextView) microCardView.findViewById(R.id.txtvFigure);

                viewHolder.mMicroCardHolders.add(microCardHolder);
            }

            playerCardHolder.setTag(viewHolder);
        }

        mSelfCardsView = (SelfCardsView) findViewById(R.id.selfCardsView);

        mCardsView = (CardsView) findViewById(R.id.cardsView);
    }

    private void setListeners() {
        mImgvMenuButton.setOnClickListener(view -> {
            if (mSlidingMenu.isMenuShowing()) {
                mSlidingMenu.showContent();
            } else {
                mSlidingMenu.showMenu();
            }
        });

        if (mIsCroupier) {
            mImgvMenuButtonRight.setOnClickListener(view -> {
                if (mSlidingMenu.isSecondaryMenuShowing()) {
                    mSlidingMenu.showContent();
                } else {
                    mSlidingMenu.showSecondaryMenu();
                }
            });
        }

        mImgvBottomArrow.setOnClickListener(view -> {
            if (mCardsView.isExpanded()) {
                mCardsView.switchState(true);
            }

            if (mSelfCardsView.isExpanded()) {
                mImgvBottomArrow.setImageResource(R.drawable.arrow_up);
            } else {
                mImgvBottomArrow.setImageResource(R.drawable.arrow_down);
            }
            mSelfCardsView.switchState(true);
        });

    }

    private void initClientService() {
        if (mClientService == null) {
            bindService(new Intent(this, ComunicationClientService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    mClientService = ((ComunicationClientService.ClientServiceBinder) iBinder).getService();
                    mClientService.registerGamerInterface(RoomActivity.this);

                    if (mClientService.isJoinedRoom()) {
                        mClientService.refreshGameState();
                    } else {
                        Dialog progressDialog = showProgressDialog(R.string.joining_room);
                        mClientService.joinRoom(new OnServerResponseListener() {
                            @Override
                            public void onServerResponse(boolean success, int serverStatus, MessageType messageType, String message) {
                                progressDialog.dismiss();
                                if (success) {
                                    GamerInfoResponse response = new Gson().fromJson(message, GamerInfoResponse.class);
                                    if (response.isAccepted()) {
                                        showDialogOneButton(R.string.response, R.string.croupier_accepted, null);
                                    } else {
                                        showDialogOneButton(R.string.response, R.string.croupier_rejected, () -> {
                                            finish();
                                        });
                                    }
                                } else {
                                    handleServerErrors(serverStatus);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            }, BIND_AUTO_CREATE);
        }
    }

    private void initGamerMenu() {
        if (mIsCroupier) {
            mImgvMenuButtonRight.setVisibility(View.VISIBLE);

            mSlidingMenu = new SlidingMenu(this);
            mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
            mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
            mSlidingMenu.setFadeDegree(0.6f);
            mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
            mSlidingMenu.setMenu(R.layout.menu_room_player);
            mSlidingMenu.setSecondaryMenu(R.layout.menu_room_croupier);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.flCroupierMenuContainer, new CroupierMenuFragment(), "croupier_fragment");
            transaction.commit();
        } else {
            mSlidingMenu = new SlidingMenu(this);
            mSlidingMenu.setMode(SlidingMenu.LEFT);
            mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
            mSlidingMenu.setFadeDegree(0.6f);
            mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
            mSlidingMenu.setMenu(R.layout.menu_room_player);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.flPlayerMenuContainer, new MainMenuFragment(), "player_fragment");
        transaction.commit();
    }

    @Override
    public void onGamersStateChanged(Gamer gamerMe, List<Gamer> gamers, int gamePhase) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (gamerMe != null && gamerMe.getCards() != null) {
                mSelfCardsView.setCards(gamerMe.getCards());

                if (gamePhase == GamePhase.cards_exchanging.ordinal()) {
                    mSelfCardsView.setQuestionsVisible(false);
                    mSelfCardsView.setExchangeMode(gamerMe.canExchangeCards());

                    if (gamerMe.canExchangeCards()) {
                        mSelfCardsView.setOnQuestionClickListener(card -> {
                            if (mSelfCardsView.getSelectedCards().size() > 0) {
                                mBtnExchangeCards.setVisibility(View.VISIBLE);
                                mBtnExchangeCards.setOnClickListener(view -> {
                                    exchangeCards();
                                });
                            } else {
                                mBtnExchangeCards.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mBtnExchangeCards.setVisibility(View.GONE);
                        mBtnExchangeCards.setOnClickListener(null);
                    }
                } else {
                    mBtnExchangeCards.setVisibility(View.GONE);
                    mBtnExchangeCards.setOnClickListener(null);
                    mSelfCardsView.setQuestionsVisible(true);
                    mSelfCardsView.setExchangeMode(false);
                    mSelfCardsView.setOnQuestionClickListener(card -> {
                        BigCardDialogFragment.newInstance(card, true).show(getFragmentManager(), "big_card_dialog");
                    });
                }
            } else {
                mSelfCardsView.setCards(null);
            }

            if (gamePhase > GamePhase.self_questions_answering.ordinal()) {
                mCardsView.setQuestionsVisible(true);
            } else {
                mCardsView.setQuestionsVisible(false);
            }

            for (int i = 0; i < gamers.size(); i++) {
                fillGamerCard(mPlayerHolders.get(i), gamers.get(i), gamePhase > GamePhase.self_questions_answering.ordinal());
            }
        });
    }

    public void answerSelfQuestion(String cardUUID, int answer) {
        mClientService.answerSelfQuestion(cardUUID, answer, (success, serverStatus, messageType, message) -> {
            if (success) {
                BasicMoveResponse response = new Gson().fromJson(message, BasicMoveResponse.class);
                if (response.getMoveStatus() == BasicMoveResponse.STATUS_SUCCESS) {
                    showDialogOneButton(R.string.success, R.string.you_answered_correctly, null);
                } else if (response.getMoveStatus() == BasicMoveResponse.STATUS_FAILURE) {
                    showDialogOneButton(R.string.failure, R.string.you_answered_wrong, null);
                } else if (response.getMoveStatus() == BasicMoveResponse.STATUS_NOT_ALLOWED) {
                    showDialogOneButton(R.string.failure, R.string.move_not_allowed, null);
                }
            } else {
                handleServerErrors(serverStatus);
            }
        });
    }

    public void declareQuestionAsCorrect(String cardUUID) {
        mClientService.declareQuestionAsCorrect(cardUUID, (success, serverStatus, messageType, message) -> {
            if (success) {
                BasicMoveResponse response = new Gson().fromJson(message, BasicMoveResponse.class);
                if (response.getMoveStatus() == BasicMoveResponse.STATUS_SUCCESS) {
                    showDialogOneButton(R.string.success, R.string.you_declared_as_correct, null);
                } else if (response.getMoveStatus() == BasicMoveResponse.STATUS_NOT_ALLOWED) {
                    showDialogOneButton(R.string.failure, R.string.move_not_allowed, null);
                }
            } else {
                handleServerErrors(serverStatus);
            }
        });
    }

    @Override
    public void onNotificationRecived(Notification notification) {
        TopNotificationDialog.newInstance(notification).show(getFragmentManager(), "top_dialog");
    }

    private void fillGamerCard(LinearLayout gamerCardHolder, Gamer gamer, boolean showCards) {
        gamerCardHolder.setVisibility(View.VISIBLE);

        GamerCardViewHolder viewHolder = (GamerCardViewHolder) gamerCardHolder.getTag();
        viewHolder.mTxtvNickname.setText(gamer.getNickname());
        ImageLoader.getInstance().displayImage(gamer.getAvatarBase64(), viewHolder.mImgvAvatar, mAvatarOptions);

        for (int i = 0; i < 5; i++) {
            MicroGameCardViewHolder cardHolder = viewHolder.mMicroCardHolders.get(i);

            if (gamer.getCards() != null && gamer.getCards().size() > i && showCards) {
                FullGameCard card = gamer.getCards().get(i);
                cardHolder.mLlContentHolder.setVisibility(View.VISIBLE);
                cardHolder.mTxtvFigure.setText(card.getPokerCard().getSign());
                cardHolder.mImgvCardColor.setImageResource(card.getPokerCard().getColorResId());
                if (card.isDeclaredCorrect()) {
                    cardHolder.mImgvPoint.setImageResource(R.drawable.green_point);
                } else {
                    cardHolder.mImgvPoint.setImageResource(R.drawable.red_point);
                }
            } else {
                cardHolder.mLlContentHolder.setVisibility(View.INVISIBLE);
            }
        }

        if (showCards) {
            gamerCardHolder.setOnClickListener(view -> {
                if (mCardsView.isExpanded()) {
                    mCardsView.setOnAnimationsEndListener(() -> {
                        mCardsView.setCards(gamer.getCards());
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            mCardsView.switchState(true);
                        }, 250l);
                    });
                    mCardsView.switchState(true);
                } else {
                    mCardsView.setCards(gamer.getCards());
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        mCardsView.switchState(true);
                        if (mSelfCardsView.isExpanded()) {
                            mSelfCardsView.switchState(true);
                            mImgvBottomArrow.setImageResource(R.drawable.arrow_up);
                        }
                    }, 250l);
                }
            });

            mCardsView.setOnQuestionClickListener(selectedCard -> {
                BigCardDialogFragment.newInstance(selectedCard, false).show(getFragmentManager(), "poker_card_dialog");
            });
        }
    }

    private void handleServerErrors(int status) {
        if (status == ServerStatusConstans.STATUS_NOT_FOUND) {
            showDialogOneButton(R.string.connection_errer, R.string.problem_connecting_croupier, null);
        } else if (status == ServerStatusConstans.STATUS_TIMEOUT) {
            showDialogOneButton(R.string.timeout, R.string.croupier_not_answered, null);
        }
    }

    private void exchangeCards() {
        showDialog(R.string.exchanging_cards, R.string.sure_exchanging_cards, () -> {
            Dialog progress = showProgressDialog(R.string.exchanging_cards);

            List<String> selectedCardsIds = new ArrayList<>();
            for (FullGameCard card : mSelfCardsView.getSelectedCards()) {
                selectedCardsIds.add(card.getUUID());
            }

            mClientService.exchangeCards(selectedCardsIds, (success, serverStatus, messageType, message) -> {
                progress.dismiss();
                if (success) {
                    CroupierAcceptResponse response = new Gson().fromJson(message, CroupierAcceptResponse.class);
                    if (response.isAccepted()) {
                        showDialogOneButton(R.string.response, R.string.croupier_accepted, null);
                    } else {
                        showDialogOneButton(R.string.response, R.string.croupier_rejected, null);
                    }
                } else {
                    handleServerErrors(serverStatus);
                }
            });
        });
    }

    private Dialog showProgressDialog(int messageResId) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(messageResId);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    private void showDialog(int titleResId, int messageResId, Runnable onPositiveClick) {
       new Handler(Looper.getMainLooper()).post(() -> {
           Dialog dialog = new AlertDialog.Builder(this)
                   .setTitle(titleResId)
                   .setMessage(messageResId)
                   .setNegativeButton(R.string.cancel, (dialogInterface, which) -> {
                       dialogInterface.dismiss();
                   })
                   .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                       dialogInterface.dismiss();
                       onPositiveClick.run();
                   })
                   .setCancelable(false)
                   .show();
           dialog.setCanceledOnTouchOutside(false);
       });
    }

    private void showDialogOneButton(int titleResId, int messageResId, Runnable onClick) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle(titleResId)
                    .setMessage(messageResId)
                    .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        if (onClick != null) {
                            onClick.run();
                        }
                    })
                    .setCancelable(false)
                    .show();
            dialog.setCanceledOnTouchOutside(false);
        });
    }

    private static class GamerCardViewHolder {
        private ImageView mImgvAvatar;
        private TextView mTxtvNickname;
        private List<MicroGameCardViewHolder> mMicroCardHolders;
    }

    private static class MicroGameCardViewHolder {
        private LinearLayout mLlContentHolder;
        private ImageView mImgvPoint;
        private ImageView mImgvCardColor;
        private TextView mTxtvFigure;
    }
}
