package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gui.dialogs.RoomsListDialogFragment;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;

public class HomeFragment extends Fragment {
    private View mRootView;
    private Button mBtnCreateRoom;
    private Button mBtnEnterRoom;

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

    private void findViews() {
        mBtnCreateRoom = (Button) mRootView.findViewById(R.id.btnCreateRoom);
        mBtnEnterRoom = (Button) mRootView.findViewById(R.id.btnEnterRoom);
    }

    private void setListeners() {
        mBtnCreateRoom.setOnClickListener(view -> mNetworkingManager.configAccessPoint("blabla"));

        mBtnEnterRoom.setOnClickListener(view -> new RoomsListDialogFragment().show(getFragmentManager(), "dialog_rooms"));
    }
}
