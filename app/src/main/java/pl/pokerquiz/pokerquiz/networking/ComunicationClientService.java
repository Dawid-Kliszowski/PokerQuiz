package pl.pokerquiz.pokerquiz.networking;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ComunicationClientService extends CommunicationBasicService {
    private ClientServiceBinder mBinder;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ClientServiceBinder extends Binder {
        public ComunicationClientService getService() {
            return ComunicationClientService.this;
        }
    }
}
