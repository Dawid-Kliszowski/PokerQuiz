package pl.pokerquiz.pokerquiz.gui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dd.CircularProgressButton;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gui.activities.RoomActivity;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;
import pl.pokerquiz.pokerquiz.networking.OnRoomConnectedListener;
import pl.pokerquiz.pokerquiz.networking.PokerRoom;

public class RoomsListDialogFragment extends DialogFragment {
    private Dialog mDialog;
    private ListView mLvRooms;
    private CircularProgressButton mCpbBackground;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity(), R.style.CustomDialog);
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragmant_rooms_list, null);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setContentView(rootView);
        mDialog.setCanceledOnTouchOutside(false);

        final WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        findViews(rootView);
        scanRooms();

        return mDialog;
    }

    private void findViews(View rootView) {
        mLvRooms = (ListView) rootView.findViewById(R.id.lvRooms);
        mCpbBackground = (CircularProgressButton) rootView.findViewById(R.id.cpbBackground);
    }

    private void scanRooms() {
        final NetworkingManager networkingManager = NetworkingManager.getInstance(getActivity());

        mLvRooms.setVisibility(View.INVISIBLE);
        mCpbBackground.setIndeterminateProgressMode(true);
        mCpbBackground.setProgress(50);

        networkingManager.getGameNetworks(rooms -> {
            if (getActivity() != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (rooms.size() > 0) {
                        List<String> roomNames = new ArrayList<>();
                        for (PokerRoom room : rooms) {
                            roomNames.add(room.getRoomName());
                        }

                        mLvRooms.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.lvitem_rooms, roomNames));
                        mLvRooms.setOnItemClickListener((adapterView, view, position, l) -> {
                            mDialog.setCanceledOnTouchOutside(false);
                            mLvRooms.setVisibility(View.INVISIBLE);
                            mCpbBackground.setIndeterminateProgressMode(true);
                            mCpbBackground.setProgress(50);

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    networkingManager.connectToRoom(new OnRoomConnectedListener(rooms.get(position)) {
                                        @Override
                                        public void onRoomConnected(final boolean success, final PokerRoom room) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                if (success) {
                                                    mCpbBackground.setProgress(100);
                                                    mCpbBackground.setCompleteText(getResources().getString(R.string.connected_to) + room.getRoomName());
                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        if (getActivity() != null) {
                                                            Intent intent = new Intent(getActivity(), RoomActivity.class);
                                                            startActivity(intent);
                                                            getActivity().finish();
                                                            dismiss();
                                                        }
                                                    }, 2000l);
                                                } else {
                                                    mDialog.setCanceledOnTouchOutside(true);
                                                    mCpbBackground.setProgress(-1);
                                                    mCpbBackground.setErrorText(getResources().getString(R.string.error_connecting_to) + room.getRoomName());
                                                }
                                            });
                                        }
                                    });
                                }
                            }, 700l);
                        });

                        mLvRooms.setVisibility(View.VISIBLE);
                        mCpbBackground.setProgress(0);
                    } else {
                        mCpbBackground.setProgress(-1);
                        mCpbBackground.setErrorText(getResources().getString(R.string.no_rooms_found));
                    }
                    mDialog.setCanceledOnTouchOutside(true);
                });
            }
        });
    }
}
