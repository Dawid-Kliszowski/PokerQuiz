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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gameLogic.Gamer;
import pl.pokerquiz.pokerquiz.gameLogic.OnServerResponseListener;
import pl.pokerquiz.pokerquiz.gui.fragments.CroupierMenuFragment;
import pl.pokerquiz.pokerquiz.gui.fragments.MainMenuFragment;
import pl.pokerquiz.pokerquiz.networking.ComunicationClientService;
import pl.pokerquiz.pokerquiz.networking.GamerInteractingInterface;
import pl.pokerquiz.pokerquiz.utils.ArcLayout;

public class RoomActivity extends Activity implements GamerInteractingInterface {
    private ComunicationClientService mClientService;

    private ImageView mImgvMenuButton;
    private ImageView mImgvMenuButtonRight;

    private SlidingMenu mSlidingMenu;
    private List<LinearLayout> mPlayerHolders;

    private DisplayImageOptions mAvatarOptions;
    private boolean mIsCroupier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mIsCroupier = PokerQuizApplication.getInstance().getServerService() != null;

        initClientService();

        findViews();
        setListeners();

        initGamerMenu();

        mAvatarOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(1000, 0))
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

    }

    private void findViews() {
        mImgvMenuButton = (ImageView) findViewById(R.id.imgvMenuButton);
        mImgvMenuButtonRight = (ImageView) findViewById(R.id.imgvMenuButtonRight);

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
                microCardHolder.mImgvCardColor = (ImageView) microCardView.findViewById(R.id.imgvPoint);
                microCardHolder.mImgvCardColor = (ImageView) microCardView.findViewById(R.id.imgvCardColor);
                microCardHolder.mTxtvFigure = (TextView) findViewById(R.id.txtvFigure);

                viewHolder.mMicroCardHolders.add(microCardHolder);
            }

            playerCardHolder.setTag(viewHolder);
        }

        ArcLayout arcLayout = (ArcLayout) findViewById(R.id.arcLayout);

        arcLayout.setOnClickListener(view -> {
            arcLayout.switchState(true);
        });

        for (int i = 0; i < 5; i++) {
            arcLayout.addView(inflater.inflate(R.layout.big_card, arcLayout, false));
        }
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
            transaction.add(R.id.flCroupierMenuContainer, new CroupierMenuFragment(), "croupier_tfragment");
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
    public void onGamersStateChanged(List<Gamer> gamers) {
        for (int i = 0; i < gamers.size(); i++) {
            fillGamerCard(mPlayerHolders.get(i), gamers.get(i));
        }
    }

    private void fillGamerCard(LinearLayout gamerCardHolder, Gamer gamer) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                gamerCardHolder.setVisibility(View.VISIBLE);

                GamerCardViewHolder viewHolder = (GamerCardViewHolder) gamerCardHolder.getTag();
                viewHolder.mTxtvNickname.setText(gamer.getNickname());
                ImageLoader.getInstance().displayImage(gamer.getAvatarBase64(), viewHolder.mImgvAvatar, mAvatarOptions);
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
