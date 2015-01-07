package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Gamer;
import pl.pokerquiz.pokerquiz.gameLogic.OnServerResponseListener;
import pl.pokerquiz.pokerquiz.gui.fragments.CroupierMenuFragment;
import pl.pokerquiz.pokerquiz.gui.fragments.MainMenuFragment;
import pl.pokerquiz.pokerquiz.gui.views.CardsView;
import pl.pokerquiz.pokerquiz.networking.ComunicationClientService;
import pl.pokerquiz.pokerquiz.networking.GamerInteractingInterface;
import pl.pokerquiz.pokerquiz.gui.views.SelfCardsView;

public class RoomActivity extends Activity implements GamerInteractingInterface {
    private ComunicationClientService mClientService;

    private ImageView mImgvMenuButton;
    private ImageView mImgvMenuButtonRight;
    private ImageView mImgvBottomArrow;

    private SlidingMenu mSlidingMenu;
    private List<LinearLayout> mPlayerHolders;
    private CardsView mCardsView;
    private SelfCardsView mSelfCardsView;

    private DisplayImageOptions mAvatarOptions;
    private boolean mIsCroupier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mIsCroupier = PokerQuizApplication.getInstance().getServerService() != null;

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

    private void findViews() {
        mImgvMenuButton = (ImageView) findViewById(R.id.imgvMenuButton);
        mImgvMenuButtonRight = (ImageView) findViewById(R.id.imgvMenuButtonRight);
        mImgvBottomArrow = (ImageView) findViewById(R.id.imgvBottomArrow);

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
                    mClientService.joinRoom(new OnServerResponseListener() {
                        @Override
                        public void onServerResponse(boolean success, int serverStatus, String messageType, String message) {

                        }
                    });
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
    public void onGamersStateChanged(Gamer gamerMe, List<Gamer> gamers) {
        new Handler(Looper.getMainLooper()).post(() -> {
            mSelfCardsView.setCards(gamerMe.getCards());
            for (int i = 0; i < gamers.size(); i++) {
                fillGamerCard(mPlayerHolders.get(i), gamers.get(i));
            }
        });
    }

    private void fillGamerCard(LinearLayout gamerCardHolder, Gamer gamer) {
        gamerCardHolder.setVisibility(View.VISIBLE);

        GamerCardViewHolder viewHolder = (GamerCardViewHolder) gamerCardHolder.getTag();
        viewHolder.mTxtvNickname.setText(gamer.getNickname());
        ImageLoader.getInstance().displayImage(gamer.getAvatarBase64(), viewHolder.mImgvAvatar, mAvatarOptions);

        for (int i = 0; i < 5; i++) {
            MicroGameCardViewHolder cardHolder = viewHolder.mMicroCardHolders.get(i);

            if (gamer.getCards() != null && gamer.getCards().size() > i) {
                FullGameCard card = gamer.getCards().get(i);
                cardHolder.mLlContentHolder.setVisibility(View.VISIBLE);
                cardHolder.mTxtvFigure.setText(card.getPokerCard().getSign());
                cardHolder.mImgvCardColor.setImageResource(card.getPokerCard().getColorResId());
            } else {
                cardHolder.mLlContentHolder.setVisibility(View.INVISIBLE);
            }
        }

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
