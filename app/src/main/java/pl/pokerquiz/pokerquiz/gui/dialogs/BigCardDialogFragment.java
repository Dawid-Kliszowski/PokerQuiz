package pl.pokerquiz.pokerquiz.gui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;
import pl.pokerquiz.pokerquiz.gui.activities.RoomActivity;
import pl.pokerquiz.pokerquiz.utils.LocaleManager;

public class BigCardDialogFragment extends DialogFragment {
    public static final String BUNDLE_KEY_GAME_CARD = "game_card";
    public static final String BUNDLE_KEY_SELF_CARD = "self_card";

    private Dialog mDialog;
    private FullGameCard mCard;
    private boolean mSelfCard;

    private LinearLayout mLlExtraOptions;
    private TextView mTxtvSign;
    private ImageView mImgvColor;
    private TextView mTxtvQuestion;
    private RadioButton mRadioAnswerFirst;
    private RadioButton mRadioAnswerSecond;
    private RadioButton mRadioAnswerThird;
    private RadioButton mRadioAnswerFourth;
    private Button mBtnAnswer;

    public static BigCardDialogFragment newInstance(FullGameCard card, boolean selfCard) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_GAME_CARD, card);
        args.putBoolean(BUNDLE_KEY_SELF_CARD, selfCard);
        BigCardDialogFragment fragment = new BigCardDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCard = (FullGameCard) getArguments().getSerializable(BUNDLE_KEY_GAME_CARD);
        mSelfCard = getArguments().getBoolean(BUNDLE_KEY_SELF_CARD);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity(), R.style.CustomDialog);
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_big_card, null);

        mDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mDialog.setContentView(rootView);
        mDialog.setCanceledOnTouchOutside(true);

        final WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        findViews(rootView);
        setViews();

        return mDialog;
    }

    private void findViews(View rootView) {
        mLlExtraOptions = (LinearLayout) rootView.findViewById(R.id.llExtraOptions);
        mTxtvSign = (TextView) rootView.findViewById(R.id.txtvSign);
        mImgvColor = (ImageView) rootView.findViewById(R.id.imgvColor);
        mTxtvQuestion = (TextView) rootView.findViewById(R.id.txtvQuestion);
        mRadioAnswerFirst = (RadioButton) rootView.findViewById(R.id.radioAnswerFirst);
        mRadioAnswerSecond = (RadioButton) rootView.findViewById(R.id.radioAnswerSecond);
        mRadioAnswerThird = (RadioButton) rootView.findViewById(R.id.radioAnswerThird);
        mRadioAnswerFourth = (RadioButton) rootView.findViewById(R.id.radioAnswerFourth);
        mBtnAnswer = (Button) rootView.findViewById(R.id.btnAnswer);
    }

    private void setViews() {
        mTxtvSign.setText(mCard.getPokerCard().getSign());
        mTxtvSign.setTextColor(mCard.getPokerCard().getFontColor());
        mImgvColor.setImageResource(mCard.getPokerCard().getColorResId());
        mTxtvQuestion.setText(mCard.getQuestion().getQuestion());
        mRadioAnswerFirst.setText(mCard.getQuestion().getAnswerFirst());
        mRadioAnswerSecond.setText(mCard.getQuestion().getAnswerSecond());
        mRadioAnswerThird.setText(mCard.getQuestion().getAnswerThird());
        mRadioAnswerFourth.setText(mCard.getQuestion().getAnswerFourth());

        if (mSelfCard && !mCard.isAnsweredByOwner()) {
            mRadioAnswerFirst.setEnabled(true);
            mRadioAnswerSecond.setEnabled(true);
            mRadioAnswerThird.setEnabled(true);
            mRadioAnswerFourth.setEnabled(true);
            mBtnAnswer.setVisibility(View.VISIBLE);
            mBtnAnswer.setOnClickListener(view -> {
                if (mRadioAnswerFirst.isChecked() || mRadioAnswerSecond.isChecked() || mRadioAnswerThird.isChecked() || mRadioAnswerFourth.isChecked()) {
                    int selectedAnswer;
                    if (mRadioAnswerFirst.isChecked()) {
                        selectedAnswer = 0;
                    } else if (mRadioAnswerSecond.isChecked()) {
                        selectedAnswer = 1;
                    } else if (mRadioAnswerThird.isChecked()) {
                        selectedAnswer = 2;
                    } else {
                        selectedAnswer = 3;
                    }

                    dismiss();
                    ((RoomActivity) getActivity()).answerSelfQuestion(mCard.getUUID(), selectedAnswer);
                }
            });
        } else if (mSelfCard && mCard.isAnsweredByOwner() && !mCard.isDeclaredCorrect()) {
            Resources localizedResources = LocaleManager.getInstance(getActivity()).getLocalizedResources();
            mBtnAnswer.setText(localizedResources.getString(R.string.declare_as_correct));
            mBtnAnswer.setOnClickListener(view -> {
                dismiss();
                ((RoomActivity) getActivity()).declareQuestionAsCorrect(mCard.getUUID());
            });
        } else if (!mSelfCard && mCard.isActive()) {
            mLlExtraOptions.setVisibility(View.VISIBLE);
        }

    }

    private void setListeners() {

    }
}
