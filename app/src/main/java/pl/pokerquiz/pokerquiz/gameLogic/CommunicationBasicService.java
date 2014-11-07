package pl.pokerquiz.pokerquiz.gameLogic;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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
import pl.pokerquiz.pokerquiz.events.ServerUnreachableEvent;

public abstract class CommunicationBasicService extends Service {
    private static final Gson GSON = new Gson();

    private HashMap<String, Thread> mChecksumConfirmationThreadsMap;
    private HashMap<String, OnServerResponseListener> mChecksumListenersMap = new HashMap<String, OnServerResponseListener>();

    private PacketListener mPacketListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setMessageSocket();
        setConfirmationSocket();
    }

    public void registerPacketListener(PacketListener packetListener) {
        mPacketListener = packetListener;
    }

    private void setMessageSocket() {
        try {
            final ServerSocket serverSocket = new ServerSocket(Constans.MESSAGE_PORT_NUMBER);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket client = serverSocket.accept();

                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            in.close();

                            SocketPacket socketPacket = GSON.fromJson(stringBuilder.toString(), SocketPacket.class);
                            sendConfirmationPacket(socketPacket.getChecksum(), ServerStatusConstans.STATUS_OK); //todo
                            mPacketListener.onPacketRecived(client.getInetAddress().toString(), socketPacket);
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        } catch (IOException ioe) {
            //todo
        }
    }

    private void setConfirmationSocket() {
        try {
            final ServerSocket serverSocket = new ServerSocket(Constans.CONFIRMATION_PORT_NUMBER);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket client = serverSocket.accept();

                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            in.close();

                            ConfirmationPacket confirmPacket = GSON.fromJson(stringBuilder.toString(), ConfirmationPacket.class);

                            removeConfirmationTimeoutThread(confirmPacket.getPacketChecksum());
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        } catch (IOException ioe) {
            //todo
        }
    }

    private void sendConfirmationPacket(final String packetChecksum, final int confirmationStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConfirmationPacket confirmPacket = new ConfirmationPacket(packetChecksum, confirmationStatus);
                    String message = GSON.toJson(confirmPacket);

                    final Socket socket = new Socket(Constans.SERVER_IP_ADDRESS, Constans.CONFIRMATION_PORT_NUMBER);
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
            }
        }).start();
    }


    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket socket = new Socket(Constans.SERVER_IP_ADDRESS, Constans.MESSAGE_PORT_NUMBER);
                    OutputStream out = socket.getOutputStream();
                    PrintWriter output = new PrintWriter(out, true);

                    SocketPacket packet = new SocketPacket(message);
                    String packetString = GSON.toJson(packet);

                    setConfirmationTimeoutThread(packet.getChecksum());

                    output.println(packetString);
                    out.close();
                    socket.close();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void removeConfirmationTimeoutThread(final String checksum) {
        Thread confirmationTimeoutThread = mChecksumConfirmationThreadsMap.get(checksum);
        if (confirmationTimeoutThread != null) {
            confirmationTimeoutThread.interrupt();
            mChecksumConfirmationThreadsMap.remove(checksum);
        }
    }

    private void setConfirmationTimeoutThread(final String checksum) {
        Thread confirmationTimeoutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Constans.SOCKET_MESSAGE_TIMEOUT);
                    mChecksumConfirmationThreadsMap.remove(checksum);
                    OnServerResponseListener listener = mChecksumListenersMap.get(checksum);
                    if (listener != null) {
                        mChecksumConfirmationThreadsMap.remove(checksum);
                        listener.onServerResponse(false, ServerStatusConstans.STATUS_NOT_FOUND, null);
                    }

                    PokerQuizApplication.getEventBus().post(new ServerUnreachableEvent());
                } catch (InterruptedException ie) {
                    // no need to handle
                }
            }
        });

        mChecksumConfirmationThreadsMap.put(checksum, confirmationTimeoutThread);
    }
}
