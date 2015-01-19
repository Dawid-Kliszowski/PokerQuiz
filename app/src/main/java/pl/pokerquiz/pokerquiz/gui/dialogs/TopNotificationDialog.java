package pl.pokerquiz.pokerquiz.gui.dialogs;

        import android.app.Dialog;
        import android.app.DialogFragment;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.view.Gravity;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.TextView;

        import java.text.SimpleDateFormat;
        import java.util.Date;

        import pl.pokerquiz.pokerquiz.R;
        import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.Notification;


public class TopNotificationDialog extends DialogFragment {
    private static final String BUNDLE_KEY_NOTIFICATION = "notification";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH : mm");

    public TopNotificationDialog() {}

    public static TopNotificationDialog newInstance(Notification notification) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_NOTIFICATION, notification);
        TopNotificationDialog fragment = new TopNotificationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        final View rootView = getActivity().getLayoutInflater().inflate(R.layout.top_notification, null);
        TextView txtvTitle = (TextView) rootView.findViewById(R.id.txtvTitle);
        TextView txtvMessage = (TextView) rootView.findViewById(R.id.txtvMessage);
        TextView txtvDate = (TextView) rootView.findViewById(R.id.txtvDate);

        Notification notification = (Notification) getArguments().getSerializable(BUNDLE_KEY_NOTIFICATION);
        txtvTitle.setText(notification.getTitle());
        txtvMessage.setText(notification.getMessage());
        txtvDate.setText(DATE_FORMAT.format(new Date(notification.getTime())));

        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setWindowAnimations(R.style.dialog_animations_slide);
        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(true);

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;

        new Thread(() -> {
            try {
                Thread.sleep(4000l);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getDialog() != null && getDialog().isShowing()) {
                        getDialog().dismiss();
                    }
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getDialog() != null && getDialog().isShowing()) {
                        getDialog().dismiss();
                    }
                });
            }
        }).start();

        return dialog;
    }
}
