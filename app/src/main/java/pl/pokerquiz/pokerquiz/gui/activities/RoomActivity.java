package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.networking.ComunicationClientService;

public class RoomActivity extends Activity {
    private ComunicationClientService mClientService;

    private SlidingMenu mSlidingMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initClientService();

        initGamerMenu();
        //initCroupierMenu();
    }

    private void initClientService() {
        bindService(new Intent(this, ComunicationClientService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mClientService = ((ComunicationClientService.ClientServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, BIND_AUTO_CREATE);
    }

    private void initGamerMenu() {
       mSlidingMenu = new SlidingMenu(this);
       mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
       mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
       mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
       mSlidingMenu.setFadeDegree(0.6f);
       mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
       mSlidingMenu.setMenu(R.layout.menu_room_gamer);
       mSlidingMenu.setSecondaryMenu(R.layout.menu_room_croupier);
    }
}
