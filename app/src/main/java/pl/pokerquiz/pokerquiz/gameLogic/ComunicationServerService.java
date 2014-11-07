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

public class ComunicationServerService extends Service{
    private ServerServiceBinder mBinder;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServerServiceBinder extends Binder {
        public ComunicationServerService getService() {
            return ComunicationServerService.this;
        }
    }
}
