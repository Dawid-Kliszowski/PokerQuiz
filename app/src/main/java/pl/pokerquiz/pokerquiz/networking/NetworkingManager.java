package pl.pokerquiz.pokerquiz.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constans;
import pl.pokerquiz.pokerquiz.gameLogic.SocketPacket;

public class NetworkingManager extends BroadcastReceiver{
    private static final String TAG = "NetworkingManager";
    private static NetworkingManager sNetworkingManager;

    private WifiManager mWifiManager;
    private OnRoomConnectedListener mConnectedListener;

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
                try {
                    WifiConfiguration wc = new WifiConfiguration();
                    wc.SSID = "\"" + listener.getRoom().getNetworkName() + "\"";
                    wc.preSharedKey = "\"" + listener.getRoom().getNetworkKey() + "\"";

                    mWifiManager.setWifiEnabled(true);

                    mConnectedListener = listener;

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


                    mWifiManager.disconnect();
                    mWifiManager.enableNetwork(networkId, true);
                    mWifiManager.reconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onRoomConnected(false, listener.getRoom());
                    }
                }
            }
        }).start();
    }

    public List<PokerRoom> getGameNetworks() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        List<PokerRoom> pokerRooms = new ArrayList<PokerRoom>();
        mWifiManager.startScan();

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
        return pokerRooms;
    }

    @Override
    public void onReceive( Context context, Intent intent )
    {
        if (mConnectedListener != null) {
            boolean success = mWifiManager.getConnectionInfo() != null &&
                    mWifiManager.getConnectionInfo().getSSID() != null &&
                    mWifiManager.getConnectionInfo().getSSID().equals(mConnectedListener.getRoom().getNetworkName());
            mConnectedListener.onRoomConnected(success, mConnectedListener.getRoom());
            //mConnectedListener = null;
        }
    }
}
