package pl.pokerquiz.pokerquiz.gameLogic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class GamerManager {
    private ComunicationClientService mService;

    public GamerManager(Context context) {
        context.bindService(new Intent(context, ComunicationClientService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mService = ((ComunicationClientService.ClientServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, context.BIND_AUTO_CREATE);
    }

}
