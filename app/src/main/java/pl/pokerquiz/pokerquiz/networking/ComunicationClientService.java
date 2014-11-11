package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import pl.pokerquiz.pokerquiz.Constans;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.datamodel.GamerInfo;

public class ComunicationClientService extends CommunicationBasicService {
    private ClientServiceBinder mBinder;
    private String mServerIp;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ClientServiceBinder extends Binder {
        public ComunicationClientService getService() {
            return ComunicationClientService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServerIp = Constans.SERVER_IP_ADDRESS;

        sendBasicUserData();
    }

    private void sendBasicUserData() {
        GamerInfo info = new GamerInfo(((PokerQuizApplication) getApplication()).getAppPrefs().getNickname());

        sendMessage(mServerIp, "", GSON.toJson(info), null);
    }
}
