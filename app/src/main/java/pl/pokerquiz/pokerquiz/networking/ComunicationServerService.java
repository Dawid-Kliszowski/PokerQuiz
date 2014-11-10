package pl.pokerquiz.pokerquiz.networking;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ComunicationServerService extends CommunicationBasicService {
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
