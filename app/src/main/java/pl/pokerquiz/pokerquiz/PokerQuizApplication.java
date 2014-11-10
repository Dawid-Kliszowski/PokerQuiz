package pl.pokerquiz.pokerquiz;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.google.common.eventbus.EventBus;

import pl.pokerquiz.pokerquiz.networking.NetworkingManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PokerQuizApplication extends Application {
    private static final EventBus EVENT_BUS = new EventBus();
    private AppPrefs mAppPrefs;
    private NetworkingManager mNetworkingManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppPrefs = new AppPrefs(this);
        mNetworkingManager = NetworkingManager.getInstance(this);
        registerReceiver(mNetworkingManager, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        registerReceiver(mNetworkingManager, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        CalligraphyConfig.initDefault("fonts/berkshire-swash.regular.ttf", R.attr.fontPath);
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(mNetworkingManager);
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(new CalligraphyContextWrapper(base));
    }

    public static EventBus getEventBus() {
        return EVENT_BUS;
    }

    public AppPrefs getAppPrefs() {
        return mAppPrefs;
    }

    public NetworkingManager getNetworkingManager() {
        return mNetworkingManager;
    }
}
