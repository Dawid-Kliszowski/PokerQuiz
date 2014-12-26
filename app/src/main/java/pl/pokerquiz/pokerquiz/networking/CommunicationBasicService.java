package pl.pokerquiz.pokerquiz.networking;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constans;
import pl.pokerquiz.pokerquiz.gameLogic.ConfirmationPacket;
import pl.pokerquiz.pokerquiz.gameLogic.OnServerResponseListener;

public abstract class CommunicationBasicService extends Service {
    protected static final Gson GSON = new Gson();
    private final String mTag = this.getClass().getSimpleName();

    private Thread mMessageSocketThread;
    private Thread mConfirmationSocketThread;
    private Thread mResponseSocketThread;

    private ServerSocket mMessageSocket;
    private ServerSocket mConfirmationSocket;
    private ServerSocket mResponseSocket;

    private HashMap<String, TimeoutThread> mChecksumTimeoutThreadsMap = new HashMap<>();
    private HashMap<String, TimeoutThread> mChecksumResponseTimeoutThreadsMap = new HashMap<>();
    private HashMap<String, OnDeliveredListener> mChecksumDeliveredListenersMap = new HashMap<>();
    private HashMap<String, OnServerResponseListener> mChecksumMessageListenersMap = new HashMap<>();

    protected abstract void onPacketReceived(String ipAddres, String messageType, String message, ResponseManager responseManager);
    protected abstract void onDeliveryFailure(String ipAddres);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            Log.d(mTag, "onCreate");
        }

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(mTag, "onDestroy");
        }

        if (mMessageSocketThread != null) {
            mMessageSocketThread.interrupt();
            mMessageSocketThread = null;
        }
        if (mConfirmationSocketThread != null) {
            mConfirmationSocketThread.interrupt();
            mConfirmationSocketThread = null;
        }
        if (mResponseSocketThread != null) {
            mResponseSocketThread.interrupt();
            mResponseSocketThread = null;
        }

        if (mMessageSocket != null) {
            try {
                mMessageSocket.close();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) {
                    ioe.printStackTrace();
                }
                throw new RuntimeException();
            }
            mMessageSocket = null;
        }

        if (mConfirmationSocket != null) {
            try {
                mConfirmationSocket.close();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) {
                    ioe.printStackTrace();
                }
                throw new RuntimeException();
            }
            mConfirmationSocket = null;
        }

        if (mResponseSocket != null) {
            try {
                mResponseSocket.close();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) {
                    ioe.printStackTrace();
                }
                throw new RuntimeException();
            }
        }

        super.onDestroy();
    }

    protected void setServerSockets() {
        setMessageSocket(Constans.SERVER_MESSAGE_PORT_NUMBER,
                Constans.CLIENT_RESPONSE_PORT_NUMBER, Constans.CLIENT_CONFIRMATION_PORT_NUMBER);
        setConfirmationSocket(Constans.SERVER_CONFIRMATION_PORT_NUMBER);
        setResponseSocket(Constans.SERVER_RESPONSE_PORT_NUMBER, Constans.CLIENT_CONFIRMATION_PORT_NUMBER);
    }

    protected void setClientSockets() {
        setMessageSocket(Constans.CLIENT_MESSAGE_PORT_NUMBER,
                Constans.SERVER_RESPONSE_PORT_NUMBER, Constans.SERVER_CONFIRMATION_PORT_NUMBER);
        setConfirmationSocket(Constans.CLIENT_CONFIRMATION_PORT_NUMBER);
        setResponseSocket(Constans.CLIENT_RESPONSE_PORT_NUMBER, Constans.SERVER_CONFIRMATION_PORT_NUMBER);
    }

    private void setMessageSocket(int port, int responsePort, int confirmationPort) {
        try {
            mMessageSocket = new ServerSocket(port);

            mMessageSocketThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket client = mMessageSocket.accept();

                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        in.close();

                        String message = stringBuilder.toString();
                        SocketPacket socketPacket = GSON.fromJson(message, SocketPacket.class);

                        String senderIp = client.getInetAddress().toString().replace("/", "");
                        sendConfirmationPacket(senderIp, confirmationPort, socketPacket.getChecksum(), ServerStatusConstans.STATUS_OK);

                        if (socketPacket.getRequiresResponse()) {
                            ResponseManager responseManager = new ResponseManager((responseType, response, deliveredListener) -> {
                                sendResponse(senderIp, responsePort, socketPacket.getChecksum(), responseType, response, deliveredListener);
                            });
                            new Thread(() -> {
                                try {
                                    Thread.sleep(socketPacket.getResponseTimeout());
                                    responseManager.onTimeout();
                                } catch (InterruptedException ie) {
                                    responseManager.onTimeout();
                                }
                            }).start();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    onPacketReceived(senderIp, socketPacket.getMessageType(), socketPacket.getMessage(), responseManager);
                                }
                            }).start();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    onPacketReceived(senderIp, socketPacket.getMessageType(), socketPacket.getMessage(), null);
                                }
                            }).start();
                        }

                        if (BuildConfig.DEBUG) {
                            Log.d(mTag, "recieved message: IP " + senderIp + ", message: " + message);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            mMessageSocketThread.start();
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) {
                ioe.printStackTrace();
            }
            throw new RuntimeException();
        }
    }

    private void setConfirmationSocket(int port) {
        try {
            mConfirmationSocket = new ServerSocket(port);

            mConfirmationSocketThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket client = mConfirmationSocket.accept();

                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        in.close();

                        String message = stringBuilder.toString();
                        ConfirmationPacket confirmPacket = GSON.fromJson(message, ConfirmationPacket.class);

                        String checksum = confirmPacket.getPacketChecksum();

                        removeConfirmationTimeoutThread(checksum);
                        OnDeliveredListener deliveredListener = mChecksumDeliveredListenersMap.get(checksum);
                        if (deliveredListener != null) {
                            mChecksumDeliveredListenersMap.remove(checksum);
                            deliveredListener.onDelivered(true, confirmPacket.getConfirmationStatus());
                        }

                        if (BuildConfig.DEBUG) {
                            Log.d(mTag, "recieved confirmation: IP " + client.getInetAddress().toString().replace("/", "") + ", message: " + message);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            mConfirmationSocketThread.start();
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) {
                ioe.printStackTrace();
            }
            throw new RuntimeException();
        }
    }

    private void setResponseSocket(int socketPort, int confirmationPort) {
        try {
            mResponseSocket = new ServerSocket(socketPort);

            mResponseSocketThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket client = mResponseSocket.accept();

                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        in.close();

                        String message = stringBuilder.toString();
                        ResponsePacket responsePacket = GSON.fromJson(message, ResponsePacket.class);

                        String senderIp = client.getInetAddress().toString().replace("/", "");
                        sendConfirmationPacket(senderIp, confirmationPort, responsePacket.getChecksum(), ServerStatusConstans.STATUS_OK);

                        removeResponseTimeoutThread(responsePacket.getRequestChecksum());
                        OnServerResponseListener listener = mChecksumMessageListenersMap.get(responsePacket.getRequestChecksum());
                        if (listener != null) {
                            mChecksumMessageListenersMap.remove(responsePacket.getRequestChecksum());
                            listener.onServerResponse(true, responsePacket.getStatus(), responsePacket.getMessageType(), responsePacket.getMessage());
                        }

                        if (BuildConfig.DEBUG) {
                            Log.d(mTag, "recieved response: IP " + client.getInetAddress().toString().replace("/", "") + ", message: " + message);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            mResponseSocketThread.start();
        } catch (IOException ioe) {
            if (BuildConfig.DEBUG) {
                ioe.printStackTrace();
            }
            throw new RuntimeException();
        }
    }

    private void sendConfirmationPacket(String ipAddress, int port, final String packetChecksum, final int confirmationStatus) {
        new Thread(() -> {
            try {
                ConfirmationPacket confirmPacket = new ConfirmationPacket(packetChecksum, confirmationStatus);
                String message = GSON.toJson(confirmPacket);

                final Socket socket = new Socket(ipAddress, port);
                OutputStream out = socket.getOutputStream();
                PrintWriter output = new PrintWriter(out, true);

                output.println(message);
                out.close();
                socket.close();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    protected void sendMessage(String ipAddress, int port, String messageType, String message, OnDeliveredListener deliveredListener, boolean requiresResponse, long responseTimeout, OnServerResponseListener responseListener) {
        new Thread(() -> {
            try {
                final Socket socket = new Socket(ipAddress, port);
                OutputStream out = socket.getOutputStream();
                PrintWriter output = new PrintWriter(out, true);

                SocketPacket packet;
                if (requiresResponse) {
                    packet = new SocketPacket(messageType, message, responseTimeout);
                    if (responseListener != null) {
                        mChecksumMessageListenersMap.put(packet.getChecksum(), responseListener);
                    }
                } else {
                    packet = new SocketPacket(messageType, message);
                }

                if (deliveredListener != null) {
                    mChecksumDeliveredListenersMap.put(packet.getChecksum(), deliveredListener);
                }

                String packetString = GSON.toJson(packet);

                setConfirmationTimeoutThread(ipAddress, packet.getChecksum());

                output.println(packetString);
                out.close();
                socket.close();

                if (BuildConfig.DEBUG) {
                    Log.d(mTag, "sent request: " + message);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendResponse(String ipAddress, int port, String requestChecksum, String messageType, String message, OnDeliveredListener deliveredListener) {
        new Thread(() -> {
            try {
                final Socket socket = new Socket(ipAddress, port);
                OutputStream out = socket.getOutputStream();
                PrintWriter output = new PrintWriter(out, true);

                ResponsePacket packet = new ResponsePacket(messageType, message, ServerStatusConstans.STATUS_OK, requestChecksum);

                if (deliveredListener != null) {
                    mChecksumDeliveredListenersMap.put(packet.getChecksum(), deliveredListener);
                }

                String packetString = GSON.toJson(packet);

                setConfirmationTimeoutThread(ipAddress, packet.getChecksum());

                output.println(packetString);
                out.close();
                socket.close();

                if (BuildConfig.DEBUG) {
                    Log.d(mTag, "sent response: " + message);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void removeConfirmationTimeoutThread(final String checksum) {
        Thread confirmationTimeoutThread = mChecksumTimeoutThreadsMap.get(checksum);
        if (confirmationTimeoutThread != null) {
            confirmationTimeoutThread.interrupt();
            mChecksumTimeoutThreadsMap.remove(checksum);
        }
    }

    private void removeResponseTimeoutThread(final String checksum) {
        Thread responseTimeoutThread = mChecksumResponseTimeoutThreadsMap.get(checksum);
        if (responseTimeoutThread != null) {
            responseTimeoutThread.interrupt();
            mChecksumResponseTimeoutThreadsMap.remove(checksum);
        }
    }

    private void setConfirmationTimeoutThread(String ipAddress, String checksum) {
        TimeoutThread timeoutThread = new TimeoutThread(ipAddress, checksum, () -> {
            try {
                Thread.sleep(Constans.DELIVERY_CONFIRMATION_TIMEOUT);
                mChecksumTimeoutThreadsMap.remove(checksum);

                OnDeliveredListener deliveredListener = mChecksumDeliveredListenersMap.get(checksum);
                if (deliveredListener != null) {
                    mChecksumDeliveredListenersMap.remove(checksum);
                    deliveredListener.onDelivered(false, ServerStatusConstans.STATUS_TIMEOUT);
                }
                OnServerResponseListener listener = mChecksumMessageListenersMap.get(checksum);
                if (listener != null) {
                    mChecksumMessageListenersMap.remove(checksum);
                    listener.onServerResponse(false, ServerStatusConstans.STATUS_TIMEOUT, null, null);
                }

                onDeliveryFailure(ipAddress);
                if (BuildConfig.DEBUG) {
                    Log.d(mTag, "packet timeout: IP " + ipAddress + ", checksum: " + checksum);
                }
            } catch (InterruptedException ie) {
                // no need to handle
            }
        });

        mChecksumTimeoutThreadsMap.put(checksum, timeoutThread);
        timeoutThread.start();
    }

    private static class TimeoutThread extends Thread {
        private String mIpAddress;
        private String mPacketChecksum;

        public TimeoutThread(String ipAddress, String packetChecksum, Runnable runnable) {
            super(runnable);

            mIpAddress = ipAddress;
            mPacketChecksum = packetChecksum;
        }

        public String getIpAddress() {
            return mIpAddress;
        }

        public String getPacketChecksum() {
            return mPacketChecksum;
        }
    }

    protected static class ResponseManager {
        private boolean mIsUsed;
        private OnResponseListener mResponseListener;
        private OnTimeoutListener mTimeoutListener;

        ResponseManager(OnResponseListener responseListener) {
            mResponseListener = responseListener;
        }

        public void setTimeoutListener(OnTimeoutListener listener) {
            mTimeoutListener = listener;
        }

        public boolean sendResponse(String messageType, String message, OnDeliveredListener deliveredListener) {
            if (!mIsUsed) {
                mIsUsed = true;
                mResponseListener.onResponse(messageType, message, deliveredListener);
            }
            return false;
        }

        void onTimeout() {
            if (!mIsUsed) {
                mIsUsed = true;
                if (mTimeoutListener != null) {
                    mTimeoutListener.onTimeout();
                }
            }
        }
    }

    static interface OnResponseListener {
        public void onResponse(String responseType, String response, OnDeliveredListener deliveredListener);
    }
}
