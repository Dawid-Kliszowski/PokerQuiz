package pl.pokerquiz.pokerquiz.gui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dd.CircularProgressButton;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;

public class CreateRoomDialogFragment extends DialogFragment {
    private Dialog mDialog;
    private CircularProgressButton mCpbBackground;
    private LinearLayout mLlForm;
    private EditText mEtRoomName;
    private Button mBtnOk;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity(), R.style.CustomDialog);
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_create_room, null);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setContentView(rootView);
        mDialog.setCanceledOnTouchOutside(true);

        final WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        findViews(rootView);
        setListeners();

        return mDialog;
    }

    private void findViews(View rootView) {
        mCpbBackground = (CircularProgressButton) rootView.findViewById(R.id.cpbBackground);
        mLlForm = (LinearLayout) rootView.findViewById(R.id.llForm);
        mEtRoomName = (EditText) rootView.findViewById(R.id.etRoomName);
        mBtnOk = (Button) rootView.findViewById(R.id.btnOk);
    }

    private void setListeners() {
        mBtnOk.setOnClickListener(view -> {

            setCancelable(false);
            mLlForm.setVisibility(View.INVISIBLE);
            mCpbBackground.setIndeterminateProgressMode(true);
            mCpbBackground.setProgress(50);

            NetworkingManager networkingManager = NetworkingManager.getInstance(getActivity());

            String roomName = mEtRoomName.getText().toString().trim();
            networkingManager.configAccessPoint(roomName);
        });
    }
}