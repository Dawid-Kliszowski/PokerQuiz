package pl.pokerquiz.pokerquiz;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.google.common.eventbus.EventBus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import pl.pokerquiz.pokerquiz.database.DatabaseHelper;
import pl.pokerquiz.pokerquiz.networking.ComunicationServerService;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;
import pl.pokerquiz.pokerquiz.utils.Base64ImageLoader;
import pl.pokerquiz.pokerquiz.utils.LocaleManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PokerQuizApplication extends Application {
    private static PokerQuizApplication sApplication;

    private static final EventBus EVENT_BUS = new EventBus();
    private AppPrefs mAppPrefs;
    private DatabaseHelper mDatabaseHelper;
    private NetworkingManager mNetworkingManager;
    private ComunicationServerService mServerService;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        mAppPrefs = new AppPrefs(this);
        mDatabaseHelper = new DatabaseHelper(this);
        mNetworkingManager = NetworkingManager.getInstance(this);
        registerReceiver(mNetworkingManager, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        registerReceiver(mNetworkingManager, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mNetworkingManager, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));

        //CalligraphyConfig.initDefault("fonts/berkshire-swash.regular.ttf", R.attr.fontPath);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .imageDownloader(new Base64ImageLoader(getApplicationContext()))
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
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

    public static AppPrefs getAppPrefs() {
        return sApplication.mAppPrefs;
    }

    public NetworkingManager getNetworkingManager() {
        return mNetworkingManager;
    }

    public void initServerService(Runnable onBindedAction) {
        bindService(new Intent(this, ComunicationServerService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mServerService = ((ComunicationServerService.ServerServiceBinder) iBinder).getService();
                onBindedAction.run();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, BIND_AUTO_CREATE);
    }

    public static ComunicationServerService getServerService() {
        return sApplication.mServerService;
    }

    public static PokerQuizApplication getInstance() {
        return sApplication;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return sApplication.mDatabaseHelper;
    }
}
