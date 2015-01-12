package pl.pokerquiz.pokerquiz.gui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import pl.pokerquiz.pokerquiz.R;

public class BigCardDialogFragment extends DialogFragment {
    private Dialog mDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity(), R.style.CustomDialog);
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_big_card, null);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setContentView(rootView);
        mDialog.setCanceledOnTouchOutside(true);

        final WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

//        findViews(rootView);
//        setListeners();

        return mDialog;
    }
}
