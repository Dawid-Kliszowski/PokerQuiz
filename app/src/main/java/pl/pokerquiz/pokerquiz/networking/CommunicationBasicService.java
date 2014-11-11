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
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.events.ConnectionTimeoutEvent;
import pl.pokerquiz.pokerquiz.gameLogic.ConfirmationPacket;
import pl.pokerquiz.pokerquiz.gameLogic.OnServerResponseListener;
import pl.pokerquiz.pokerquiz.gameLogic.PacketListener;
import pl.pokerquiz.pokerquiz.gameLogic.ServerStatusConstans;
import pl.pokerquiz.pokerquiz.gameLogic.SocketPacket;

public abstract class CommunicationBasicService extends Service {
    protected static final Gson GSON = new Gson();
    private final String mTag = this.getClass().getSimpleName();

    private Thread mMessageSocketThread;
    private Thread mConfirmationSocketThread;
    private ServerSocket mMessageSocket;
    private ServerSocket mConfirmationSocket;

    private HashMap<String, TimeoutThread> mChecksumTimeoutThreadsMap = new HashMap<>();
    private HashMap<String, OnServerResponseListener> mChecksumListenersMap = new HashMap<>();

    private PacketListener mPacketListener;

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
        setMessageSocket();
        setConfirmationSocket();
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

        if (mMessageSocket != null) {
            try {
                mMessageSocket.close();
            } catch (IOException ioe) {
                throw new RuntimeException();
            }
            mMessageSocket = null;
        }

        if (mConfirmationSocket != null) {
            try {
                mConfirmationSocket.close();
            } catch (IOException ioe) {
                throw new RuntimeException();
            }
            mConfirmationSocket = null;
        }

        super.onDestroy();
    }

    public void registerPacketListener(PacketListener packetListener) {
        mPacketListener = packetListener;
    }

    private void setMessageSocket() {
        try {
            mMessageSocket = new ServerSocket(Constans.MESSAGE_PORT_NUMBER);

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
                        sendConfirmationPacket(senderIp, socketPacket.getChecksum(), ServerStatusConstans.STATUS_OK);

                        if (mPacketListener != null) {
                            new Thread(() -> mPacketListener.onPacketRecived(senderIp, socketPacket.getMessageType(), socketPacket.getMessage())).start();
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

    private void setConfirmationSocket() {
        try {
            mConfirmationSocket = new ServerSocket(Constans.CONFIRMATION_PORT_NUMBER);

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

                        removeConfirmationTimeoutThread(confirmPacket.getPacketChecksum());
                        OnServerResponseListener listener = mChecksumListenersMap.get(confirmPacket.getPacketChecksum());
                        if (listener != null) {
                            mChecksumListenersMap.remove(confirmPacket.getConfirmationStatus());
                            listener.onServerResponse(true, confirmPacket.getConfirmationStatus());
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

    private void sendConfirmationPacket(String ipAddress, final String packetChecksum, final int confirmationStatus) {
        new Thread(() -> {
            try {
                ConfirmationPacket confirmPacket = new ConfirmationPacket(packetChecksum, confirmationStatus);
                String message = GSON.toJson(confirmPacket);

                final Socket socket = new Socket(ipAddress, Constans.CONFIRMATION_PORT_NUMBER);
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


    public void sendMessage(String ipAddress, String messageType, String message, OnServerResponseListener listener) {
        new Thread(() -> {
            try {
                final Socket socket = new Socket(ipAddress, Constans.MESSAGE_PORT_NUMBER);
                OutputStream out = socket.getOutputStream();
                PrintWriter output = new PrintWriter(out, true);

                SocketPacket packet = new SocketPacket(messageType, message);
                String packetString = GSON.toJson(packet);

                if (listener != null) {
                    mChecksumListenersMap.put(packet.getChecksum(), listener);
                }
                setConfirmationTimeoutThread(ipAddress, packet.getChecksum());

                output.println(packetString);
                out.close();
                socket.close();
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

    private void setConfirmationTimeoutThread(String ipAddress, String checksum) {
        TimeoutThread timeoutThread = new TimeoutThread(ipAddress, checksum, () -> {
            try {
                Thread.sleep(Constans.SOCKET_MESSAGE_TIMEOUT);
                mChecksumTimeoutThreadsMap.remove(checksum);
                OnServerResponseListener listener = mChecksumListenersMap.get(checksum);
                if (listener != null) {
                    mChecksumListenersMap.remove(checksum);
                    listener.onServerResponse(false, ServerStatusConstans.STATUS_TIMEOUT);
                }

                PokerQuizApplication.getEventBus().post(new ConnectionTimeoutEvent(ipAddress));
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
}
