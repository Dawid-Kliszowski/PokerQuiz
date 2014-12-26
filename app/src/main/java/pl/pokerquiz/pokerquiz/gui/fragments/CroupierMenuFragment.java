package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.GamerInfo;
import pl.pokerquiz.pokerquiz.networking.AcceptingManager;
import pl.pokerquiz.pokerquiz.networking.ComunicationServerService;
import pl.pokerquiz.pokerquiz.networking.CroupierInteractingInterface;
import pl.pokerquiz.pokerquiz.networking.OnTimeoutListener;

public class CroupierMenuFragment extends Fragment implements CroupierInteractingInterface {
    private Button mBtnNewPlayersAuto;

    private ComunicationServerService mServerService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServerService = ((PokerQuizApplication) getActivity().getApplication()).getServerService();
        mServerService.registerCroupierInterface(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment needs layoutInflater from Activity because of Lollipop broken inflation
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_croupier_menu, container, false);

        findViews(rootView);

        return rootView;
    }

    private void findViews(View rootView) {
        mBtnNewPlayersAuto = (Button) rootView.findViewById(R.id.btnNewPlayersAuto);
    }

    private void setListeners() {
        mBtnNewPlayersAuto.setOnClickListener(view -> {

        });
    }

    @Override
    public void onGamerConnected(GamerInfo gamerInfo, AcceptingManager acceptManager) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("New gamer")
                    .setMessage(gamerInfo.getNick() + " wants to join this room")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            acceptManager.setAccept(true);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("REJECT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            acceptManager.setAccept(false);
                            dialogInterface.dismiss();
                        }
                    });

            new Handler(Looper.getMainLooper()).post(() -> {
                AlertDialog dialog = builder.show();
                acceptManager.setTimeoutListener(new OnTimeoutListener() {
                    @Override
                    public void onTimeout() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            });
        }
    }
}
