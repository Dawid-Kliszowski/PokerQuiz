package pl.pokerquiz.pokerquiz.gui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;
import pl.pokerquiz.pokerquiz.gameLogic.CardFigure;
import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;

public class PokerCardBigView extends RelativeLayout {
    private TextView mTxtvSign;
    private ImageView mImgvColorBottom;
    private ImageView mImgvColorRight;
    private LinearLayout mLlContent;
    private CardContentView mCardContentView;
    private LinearLayout mLlQuestionHolder;
    private TextView mTxtvQuestion;
    private TextView mTxtvAnswerFirst;
    private TextView mTxtvAnswerSecond;
    private TextView mTxtvAnswerThird;
    private TextView mTxtvAnswerFourth;

    private FullGameCard mFullGameCard;

    public PokerCardBigView(Context context) {
        super(context);
        init();
    }

    public PokerCardBigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerCardBigView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.big_card, this);

        mTxtvSign = (TextView) findViewById(R.id.txtvSign);
        mImgvColorBottom = (ImageView) findViewById(R.id.imgvColorBottom);
        mImgvColorRight = (ImageView) findViewById(R.id.imgvColorRight);
        mLlContent = (LinearLayout) findViewById(R.id.llContent);
        mLlQuestionHolder = (LinearLayout) findViewById(R.id.llQuestionHolder);
        mTxtvQuestion = (TextView) findViewById(R.id.txtvQuestion);
        mTxtvAnswerFirst = (TextView) findViewById(R.id.txtvAnswerFirst);
        mTxtvAnswerSecond = (TextView) findViewById(R.id.txtvAnswerSecond);
        mTxtvAnswerThird = (TextView) findViewById(R.id.txtvAnswerThird);
        mTxtvAnswerFourth = (TextView) findViewById(R.id.txtvAnswerFourth);

        mCardContentView = new CardContentView(getContext());
        mLlContent.addView(mCardContentView);
    }

    public void setBottomColorVisible() {
        mImgvColorBottom.setVisibility(VISIBLE);
        mImgvColorRight.setVisibility(INVISIBLE);
    }

    public void setRightColorVisible() {
        mImgvColorBottom.setVisibility(INVISIBLE);
        mImgvColorRight.setVisibility(VISIBLE);
    }

    public void setPokerCard(FullGameCard fullGameCard) {
        mFullGameCard = fullGameCard;

        mTxtvSign.setText(mFullGameCard.getPokerCard().getSign());
        mTxtvSign.setTextColor(mFullGameCard.getPokerCard().getFontColor());
        mImgvColorBottom.setImageResource(mFullGameCard.getPokerCard().getColorResId());
        mImgvColorRight.setImageResource(mFullGameCard.getPokerCard().getColorResId());

        mCardContentView.setFigureImageBitmapParams(mFullGameCard.getPokerCard().getFigureResId(),
                mFullGameCard.getPokerCard().getFigureIconParams());

        if (mFullGameCard.getQuestion() != null) {
            QuizQuestion question = mFullGameCard.getQuestion();
            mTxtvQuestion.setText(question.getQuestion());
            mTxtvAnswerFirst.setText(question.getAnswerFirst());
            mTxtvAnswerSecond.setText(question.getAnswerSecond());
            mTxtvAnswerThird.setText(question.getAnswerThird());
            mTxtvAnswerFourth.setText(question.getAnswerFourth());
        }

    }

    public void setQuestionVisible(boolean visible) {
        if (visible) {
            mLlQuestionHolder.setVisibility(VISIBLE);
        } else {
            mLlQuestionHolder.setVisibility(INVISIBLE);
        }
    }

    public FullGameCard getCard() {
        return mFullGameCard;
    }

    private static class CardContentView extends View {
        private List<RectF> mRects = new ArrayList<>();
        private List<CardFigure.IconPlaceParam> mIconParams;
        private Bitmap mFigureImageBitmap;

        public CardContentView(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if (mFigureImageBitmap != null && mIconParams != null) {
                float width = MeasureSpec.getSize(widthMeasureSpec);
                float height = MeasureSpec.getSize(heightMeasureSpec);
                float ratio = (float) mFigureImageBitmap.getWidth() / (float) mFigureImageBitmap.getHeight();

                mRects.clear();

                for (CardFigure.IconPlaceParam iconPlace : mIconParams) {
                    mRects.add(new RectF(width * iconPlace.getLeft(),
                            height * iconPlace.getTop(),
                            width * iconPlace.getLeft() + height * iconPlace.getSize() * ratio,
                            height * (iconPlace.getTop() + iconPlace.getSize())));
                }
            }
        }

        void setFigureImageBitmapParams (int figureResId, List<CardFigure.IconPlaceParam> iconParams) {
            mFigureImageBitmap = BitmapFactory.decodeResource(getResources(), figureResId);
            mIconParams = iconParams;

            float width = getWidth();
            float height = getHeight();
            float ratio = (float) mFigureImageBitmap.getWidth() / (float) mFigureImageBitmap.getHeight();

            mRects.clear();

            for (CardFigure.IconPlaceParam iconPlace : mIconParams) {
                mRects.add(new RectF(width * iconPlace.getLeft(),
                        height * iconPlace.getTop(),
                        width * iconPlace.getLeft() + height * iconPlace.getSize() * ratio,
                        height * (iconPlace.getTop() + iconPlace.getSize())));
            }

            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas) {
            for (RectF rect : mRects) {
                canvas.drawBitmap(mFigureImageBitmap, null, rect, null);
            }
        }
    }
}
