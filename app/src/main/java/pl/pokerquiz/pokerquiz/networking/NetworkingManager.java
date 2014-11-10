package pl.pokerquiz.pokerquiz.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constans;

public class NetworkingManager extends BroadcastReceiver{
    private static final String TAG = "NetworkingManager";
    private static NetworkingManager sNetworkingManager;

    private WifiManager mWifiManager;
    private OnRoomConnectedListener mConnectedListener;
    private OnRoomsScannedListener mScannedListener;
    private Runnable mWifiDisconnectedListener;
    private Runnable mWifiEnabledListener;

    public static NetworkingManager getInstance(Context context) {
        if (sNetworkingManager == null) {
            sNetworkingManager = new NetworkingManager(context.getApplicationContext());
        }
        return sNetworkingManager;
    }

    private NetworkingManager(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    }

    public boolean configAccessPoint(String roomName) {
        try {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
            String apName = roomName + Constans.WIFI_NETWORK_POSTFIX;

            WifiConfiguration config = new WifiConfiguration();
            config.SSID = apName;
            config.preSharedKey = getMd5(apName).substring(0, 8);
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

            Method setApEnabledMethod = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setApEnabledMethod.invoke(mWifiManager, config, true);
            return true;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getMyIp() {
        return Formatter.formatIpAddress(mWifiManager.getConnectionInfo().getIpAddress());
    }

    private InetAddress getBroadcastAddress() throws IOException {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (intf.getDisplayName().contains("wlan0") ||
                            intf.getDisplayName().contains("eth0") || intf.getDisplayName().contains("ap0"))) {
                        for (InterfaceAddress address : NetworkInterface.getByInetAddress(inetAddress).getInterfaceAddresses()) {
                            if (address.getBroadcast() != null) {
                                return address.getBroadcast();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isApOn(Context context)
    {
        WifiManager wifimanager= (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        try
        {
            Method method =wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getMd5(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();

        StringBuffer hexString = new StringBuffer();
        for (byte aMessageDigest : messageDigest) {
            hexString.append(Integer.toHexString(0xFF & aMessageDigest));
        }
        return hexString.toString();
    }


    public void connectToRoom(final OnRoomConnectedListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                resetWifiConnection(new Runnable() {
                    @Override
                    public void run() {
                        WifiConfiguration wc = new WifiConfiguration();
                        wc.SSID = "\"" + listener.getRoom().getNetworkName() + "\"";
                        wc.preSharedKey = "\"" + listener.getRoom().getNetworkKey() + "\"";

                        mConnectedListener = listener;

                        mWifiManager.setWifiEnabled(true);

                        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
                        for (WifiConfiguration config : configuredNetworks) {
                            if(config.SSID != null && config.SSID.equals("\"" + listener.getRoom().getNetworkName() + "\"")) {
                                mWifiManager.disconnect();
                                mWifiManager.enableNetwork(config.networkId, true);
                                mWifiManager.reconnect();

                                return;
                            }
                        }

                        int networkId = mWifiManager.addNetwork(wc);
                        mWifiManager.saveConfiguration();

                        mWifiManager.enableNetwork(networkId, true);
                        mWifiManager.reconnect();
                    }
                });
            }
        }).start();
    }

    public void getGameNetworks(OnRoomsScannedListener listener) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mScannedListener = listener;
        mWifiManager.startScan();

    }

    private void resetWifiConnection(Runnable onWifiResetAction) {
        if (mWifiManager.isWifiEnabled()) {
            if (mWifiManager.getConnectionInfo().getSSID() != null && !mWifiManager.getConnectionInfo().getSSID().equals("<unknown ssid>")) {
                mWifiManager.disconnect();
                mWifiDisconnectedListener = onWifiResetAction;
            } else {
                onWifiResetAction.run();
            }
        } else {
            mWifiManager.setWifiEnabled(true);
            mWifiEnabledListener = onWifiResetAction;
        }
    }

    @Override
    public synchronized void onReceive( Context context, Intent intent )
    {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onReceive(): " + mWifiManager.getConnectionInfo().getSSID());
        }
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.STATE_CHANGE") && mWifiManager.isWifiEnabled() && mWifiEnabledListener != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onReceive(): option 1");
            }
            mWifiEnabledListener.run();
            mWifiEnabledListener = null;
        } else if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.STATE_CHANGE") && mWifiManager.isWifiEnabled() &&
                mWifiManager.getConnectionInfo().getSSID() != null && mWifiManager.getConnectionInfo().getSSID().equals("<unknown ssid>") && mWifiDisconnectedListener != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onReceive(): option 2");
            }
            mWifiDisconnectedListener.run();
            mWifiDisconnectedListener = null;
        } else if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.STATE_CHANGE") && mConnectedListener != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onReceive(): option 3");
            }
            boolean success = mWifiManager.getConnectionInfo() != null &&
                    mWifiManager.getConnectionInfo().getSSID() != null &&
                    mWifiManager.getConnectionInfo().getSSID().equals("\"" + mConnectedListener.getRoom().getNetworkName() + "\"");
            mConnectedListener.onRoomConnected(success, mConnectedListener.getRoom());
            mConnectedListener = null;
        } else if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.SCAN_RESULTS") && mScannedListener != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onReceive(): option 4");
            }
            List<PokerRoom> pokerRooms = new ArrayList<PokerRoom>();
            for (ScanResult scanResult : mWifiManager.getScanResults()) {
                String ssid = scanResult.SSID;
                String postfix = Constans.WIFI_NETWORK_POSTFIX;

                if (ssid.length() > postfix.length() && ssid.substring(ssid.length() - postfix.length(), ssid.length()).equals(postfix)) {
                    try {
                        pokerRooms.add(new PokerRoom(ssid.substring(0, ssid.length() - postfix.length()), ssid,  getMd5(ssid).substring(0, 8)));
                    } catch(NoSuchAlgorithmException nsae) {
                        nsae.printStackTrace();
                    }
                }
            }
            mScannedListener.onRoomsScanned(pokerRooms);
            mScannedListener = null;
        }
    }
}
