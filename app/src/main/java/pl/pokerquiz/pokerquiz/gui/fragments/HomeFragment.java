package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gui.activities.MainActivity;
import pl.pokerquiz.pokerquiz.gui.activities.RoomActivity;
import pl.pokerquiz.pokerquiz.gui.dialogs.CreateRoomDialogFragment;
import pl.pokerquiz.pokerquiz.gui.dialogs.RoomsListDialogFragment;
import pl.pokerquiz.pokerquiz.networking.ComunicationClientService;
import pl.pokerquiz.pokerquiz.networking.ComunicationServerService;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;

public class HomeFragment extends Fragment {
    private View mRootView;
    private Button mBtnCreateRoom;
    private Button mBtnEnterRoom;
    private Button mBtnQuizQuestions;
    private Button mBtnSettings;

    private NetworkingManager mNetworkingManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkingManager = ((PokerQuizApplication) getActivity().getApplication()).getNetworkingManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment needs layoutInflater from Activity because of Lollipop broken inflation
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_home, container, false);

        findViews();
        setListeners();

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setViews();
    }

    private void findViews() {
        mBtnCreateRoom = (Button) mRootView.findViewById(R.id.btnCreateRoom);
        mBtnEnterRoom = (Button) mRootView.findViewById(R.id.btnEnterRoom);
        mBtnQuizQuestions = (Button) mRootView.findViewById(R.id.btnQuizQuestions);
        mBtnSettings = (Button) mRootView.findViewById(R.id.btnSettings);
    }

    private void setViews() {
        if (PokerQuizApplication.getInstance().getServerService() != null) {
            mBtnCreateRoom.setText(R.string.close_your_room);
            mBtnEnterRoom.setText(R.string.enter_your_room);
        }
    }

    private void setListeners() {
        mBtnCreateRoom.setOnClickListener(view -> {
            if (PokerQuizApplication.getInstance().getServerService() != null) {
                //todo
            } else {
                new CreateRoomDialogFragment().show(getFragmentManager(), "dialog_new_room");
            }
        });

        mBtnEnterRoom.setOnClickListener(view -> {
            if (PokerQuizApplication.getInstance().getServerService() != null) {
                startActivity(new Intent(getActivity(), RoomActivity.class));
            } else {
                new RoomsListDialogFragment().show(getFragmentManager(), "dialog_rooms");
            }
        });

        mBtnQuizQuestions.setOnClickListener(view -> {
            ((MainActivity) getActivity()).setFragment(new CategoriesListFragment(), false);
        });

        mBtnSettings.setOnClickListener(view -> {
            ((MainActivity) getActivity()).setFragment(new SettingsFragment(), false);
        });
    }
}
