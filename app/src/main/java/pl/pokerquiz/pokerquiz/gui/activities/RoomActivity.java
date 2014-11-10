package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.networking.ComunicationClientService;

public class RoomActivity extends Activity {
    private ComunicationClientService mClientService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initClientService();
    }

    private void initClientService() {
        bindService(new Intent(this, ComunicationClientService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mClientService = ((ComunicationClientService.ClientServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, BIND_AUTO_CREATE);
    }
}
