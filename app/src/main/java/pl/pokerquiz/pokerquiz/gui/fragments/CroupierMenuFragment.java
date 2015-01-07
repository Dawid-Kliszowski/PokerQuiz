package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.GamerInfo;
import pl.pokerquiz.pokerquiz.networking.AcceptingManager;
import pl.pokerquiz.pokerquiz.networking.ComunicationServerService;
import pl.pokerquiz.pokerquiz.networking.CroupierInteractingInterface;
import pl.pokerquiz.pokerquiz.networking.OnTimeoutListener;
import pl.pokerquiz.pokerquiz.utils.LocaleManager;

public class CroupierMenuFragment extends Fragment implements CroupierInteractingInterface {
    private Button mBtnNewPlayersAuto;
    private Button mBtnDealCards;

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
        setListeners();

        return rootView;
    }

    private void findViews(View rootView) {
        mBtnNewPlayersAuto = (Button) rootView.findViewById(R.id.btnNewPlayersAuto);
        mBtnDealCards = (Button) rootView.findViewById(R.id.btnDealCards);
    }

    private void setListeners() {
        mBtnNewPlayersAuto.setOnClickListener(view -> {
            //todo
        });

        mBtnDealCards.setOnClickListener(view -> {
            mServerService.dealCards();
        });
    }

    @Override
    public void onGamerConnected(GamerInfo gamerInfo, AcceptingManager acceptManager) {
        if (getActivity() != null) {
            Resources localizedRes = LocaleManager.getInstance(getActivity()).getLocalizedResources();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.new_gamer)
                    .setMessage(gamerInfo.getNick() + " " + localizedRes.getString(R.string.wants_to_join))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            acceptManager.setAccept(true);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
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
