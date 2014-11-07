package pl.pokerquiz.pokerquiz;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.gameLogic.ComunicationServerService;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;
import pl.pokerquiz.pokerquiz.networking.OnRoomConnectedListener;
import pl.pokerquiz.pokerquiz.networking.OnSocketMessageListener;
import pl.pokerquiz.pokerquiz.networking.PokerRoom;


public class MainActivity extends Activity {
    private EditText mEtNetworkName;
    private TextView mTxtvNetwork;
    private TextView mTxtvMessage;
    private TextView mTxtvListNetworks;
    private TextView mTxtvSocketMessage;
    private EditText mEtSocketMessage;
    private ListView mLvRooms;

    private NetworkingManager mNetworkingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        configureNetworkManager();
        setListeners();
    }

    private void findViews() {
        mEtNetworkName = (EditText) findViewById(R.id.etNetworkName);
        mTxtvNetwork = (TextView) findViewById(R.id.txtvNetwork);
        mTxtvMessage = (TextView) findViewById(R.id.txtvMessage);
        mTxtvListNetworks = (TextView) findViewById(R.id.txtvListNetworks);
        mTxtvSocketMessage = (TextView) findViewById(R.id.txtvSocketMesage);
        mEtSocketMessage = (EditText) findViewById(R.id.etSocketMessage);
        mLvRooms = (ListView) findViewById(R.id.lvRooms);
    }

    private void setListeners() {
        mTxtvNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNetworkingManager.configAccessPoint(mEtNetworkName.getEditableText().toString());
            }
        });

        mTxtvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mNetworkingManager.sendBroadcastMessage(mEtSocketMessage.getEditableText().toString());
            }
        });

        mTxtvListNetworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<PokerRoom> rooms  = mNetworkingManager.getGameNetworks();

                List<String> roomNames = new ArrayList<String>();
                for (PokerRoom room : rooms) {
                    roomNames.add(room.getRoomName());
                }
                mLvRooms.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.lvitem_rooms, roomNames));
                mLvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        mNetworkingManager.connectToRoom(new OnRoomConnectedListener(rooms.get(position)) {
                            @Override
                            public void onRoomConnected(final boolean success, final PokerRoom room) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String roomName = "";
                                        if (room != null) {
                                            roomName = room.getRoomName();
                                        }
                                        mTxtvSocketMessage.setText("success:" + success + ", room: " + roomName);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void configureNetworkManager() {
        mNetworkingManager = NetworkingManager.getInstance(this);
        registerReceiver(mNetworkingManager, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        //Comunication
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNetworkingManager);
        super.onDestroy();
    }
}
