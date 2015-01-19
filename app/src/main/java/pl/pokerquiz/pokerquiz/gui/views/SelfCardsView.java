package pl.pokerquiz.pokerquiz.gui.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;

public class SelfCardsView extends ViewGroup {
    public static final float DEFAULT_FROM_DEGREES = 180.0f;
    public static final float DEFAULT_TO_DEGREES = 360.0f;
    public static final int CARDS_COUNT = 5;
    public static final int DEFAULT_HEIGHT_ON_NOT_EXPANDED = 45;
    public static final float RADIUS_RATIO = 0.2f;

    private int mChildHeight;
    private int mChildWidth;
    private float mFromDegrees = DEFAULT_FROM_DEGREES;
    private float mToDegrees = DEFAULT_TO_DEGREES;
    private int mRadius;
    private boolean mExpanded = false;
    private float mChildDimensRatio = 0.7f;
    private int mHeightOnNotExpanded;
    private OnAnimationsEndListener mAnimationsEndListener;
    private List<PokerCardBigView> mChildViews = new ArrayList<>();
    private SparseBooleanArray mSelectionArray = new SparseBooleanArray();
    private OnQuestionClickedListener mOnQuestionClickListener;
    private boolean mExchangeMode;
    private boolean mQuestionsVisible;

    private List<FullGameCard> mCards;

    public SelfCardsView(Context context) {
        super(context);
        init();
    }

    public SelfCardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHeightOnNotExpanded = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT_ON_NOT_EXPANDED, getResources().getDisplayMetrics());

        if (mCards != null && mCards.size() > 0) {
            fillChildViews();
        }
    }

    private Rect computeChildFrame(boolean expanded, int index, final float degrees) {

        int bottomOffset;
        if (expanded) {
            bottomOffset = (int) (- mRadius * Math.sin(Math.toRadians(degrees)));
        } else {
            bottomOffset = - mChildHeight + mHeightOnNotExpanded;
        }
        final int leftOffset = (int) ((float) index * (((float) getWidth() - (float) mChildWidth) / (float) (getChildCount() - 1)));

        return new Rect(leftOffset, getHeight() - mChildHeight - bottomOffset, leftOffset + mChildWidth, getHeight() - bottomOffset);
    }

    public void setOnQuestionClickListener(OnQuestionClickedListener listener) {
        mOnQuestionClickListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        mRadius = (int) (((float) layoutHeight) * RADIUS_RATIO);
        mChildHeight = layoutHeight - mRadius;
        mChildWidth = (int) ((float) mChildHeight * mChildDimensRatio);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        for (View childView : mChildViews) {
            childView.measure(MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = mChildViews.size();
        final float perDegrees = (mToDegrees - mFromDegrees) / (childCount - 1);

        float degrees = mFromDegrees;

        for (int i = 0; i < childCount; i++) {
            Rect frame = computeChildFrame(mExpanded, i, degrees);
            degrees += perDegrees;

            mChildViews.get(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void setCards(List<FullGameCard> cards) {
        mCards = cards;
        if (cards != null && cards.size() > 0) {
            fillChildViews();
            for (int i = 0; i < mChildViews.size(); i++) {
                mChildViews.get(i).setPokerCard(cards.get(i));
            }
        } else {
            removeChildViews();
        }
    }

    public void setExchangeMode(boolean exchange) {
        mExchangeMode = exchange;
        if (!exchange) {
            mSelectionArray.clear();
        }
        for (PokerCardBigView cardView : mChildViews) {
            cardView.setExchangeIconVisible(exchange);
            if (!exchange) {
                cardView.setOverlayVisible(false);
            }
        }
        resetCardsLayersOrder();
    }

    public void setQuestionsVisible(boolean visible) {
        mQuestionsVisible = visible;
    }

    private void fillChildViews() {
        if (mChildViews.size() == 0) {
            for (int i = 0; i < CARDS_COUNT; i++) {
                PokerCardBigView bigCardView = new PokerCardBigView(getContext());
                mChildViews.add(bigCardView);
                addView(bigCardView);
                bigCardView.setQuestionVisible(false);

                final int finalI = i;
                bigCardView.setOnClickListener(view -> {
                    resetCardsLayersOrder();
                    removeView(bigCardView);
                    addView(bigCardView);

                    if (mExchangeMode) {
                        boolean currentState = !mSelectionArray.get(finalI);
                        mSelectionArray.put(finalI, currentState);
                        bigCardView.setOverlayVisible(currentState);
                        if (mOnQuestionClickListener != null) {
                            mOnQuestionClickListener.onQuestionClicked(bigCardView.getCard());
                        }
                    } else if (mQuestionsVisible) {
                        if (bigCardView.getCard() != null && bigCardView.getCard().getQuestion() != null) {
                            bigCardView.setOnClickListener(view1 -> {
                                bigCardView.setQuestionVisible(true);
                                if (bigCardView.getCard().isActive()) {
                                    bigCardView.setOnClickListener(view3 -> {
                                        mOnQuestionClickListener.onQuestionClicked(bigCardView.getCard());
                                    });
                                } else {
                                    bigCardView.setOnClickListener(null);
                                }
                            });
                        }
                    }

                });
            }
            invalidate();
        }
    }

    private void removeChildViews() {
        for (View childView : mChildViews) {
            removeView(childView);
        }
        mChildViews.clear();
    }

    private static long computeStartOffset(final int childCount, final boolean expanded, final int index,
                                           final float delayPercent, final long duration, Interpolator interpolator) {
        final float delay = delayPercent * duration;
        final long viewDelay = (long) (getTransformedIndex(expanded, childCount, index) * delay);
        final float totalDelay = delay * childCount;

        float normalizedDelay = viewDelay / totalDelay;
        normalizedDelay = interpolator.getInterpolation(normalizedDelay);

        return (long) (normalizedDelay * totalDelay);
    }

    private static int getTransformedIndex(final boolean expanded, final int count, final int index) {
        if (expanded) {
            return count - 1 - index;
        }

        return index;
    }

    private static Animation createExpandAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
                                                   long startOffset, long duration, Interpolator interpolator) {
        Animation animation = new TranslateAnimation(0f, toXDelta, 0f, toYDelta);
        animation.setStartOffset(startOffset);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);

        return animation;
    }

    private static Animation createShrinkAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
                                                   long startOffset, long duration, Interpolator interpolator) {

        Animation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        translateAnimation.setStartOffset(startOffset);
        translateAnimation.setDuration(duration);
        translateAnimation.setInterpolator(interpolator);
        translateAnimation.setFillAfter(true);

        return translateAnimation;
    }

    private void bindChildAnimation(final View child, final int index, final long duration) {
        final boolean expanded = mExpanded;

        final int childCount = getChildCount();
        final float perDegrees = (mToDegrees - mFromDegrees) / (childCount - 1);
        Rect frame = computeChildFrame(!mExpanded, index, mFromDegrees + index * perDegrees);

        final int toXDelta = frame.left - child.getLeft();
        final int toYDelta = frame.top - child.getTop();

        Interpolator interpolator = mExpanded ? new AccelerateInterpolator() : new DecelerateInterpolator();
        final long startOffset = computeStartOffset(childCount, false, index, 0.1f, duration, interpolator);

        Animation animation = mExpanded ? createShrinkAnimation(0, toXDelta, 0, toYDelta, startOffset, duration,
                interpolator) : createExpandAnimation(0, toXDelta, 0, toYDelta, startOffset, duration, interpolator);

        final boolean isLast = getTransformedIndex(expanded, childCount, index) == childCount - 1;
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isLast) {
                    postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            onAllAnimationsEnd();
                        }
                    }, 0);
                }
            }
        });

        child.setAnimation(animation);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void switchState(final boolean showAnimation) {
        if (showAnimation) {
            for (int i = 0; i < mChildViews.size(); i++) {
                bindChildAnimation(mChildViews.get(i), i, 240);
            }
        }

        mExpanded = !mExpanded;

        for (int i = 0; i < getChildCount(); i++) {
            if (mExpanded) {
                ((PokerCardBigView) getChildAt(i)).setBottomColorVisible();
            } else {
                ((PokerCardBigView) getChildAt(i)).setRightColorVisible();
            }
        }

        if (!showAnimation) {
            requestLayout();
        }

        invalidate();
    }

    private void onAllAnimationsEnd() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).clearAnimation();
        }

        requestLayout();

        if (mAnimationsEndListener != null) {
            mAnimationsEndListener.onAnimationsEnd();
            mAnimationsEndListener = null;
        }
    }

    private void resetCardsLayersOrder() {
        for (View childView : mChildViews) {
            removeView(childView);
        }

        for (int i = 0; i < mChildViews.size(); i++) {
            PokerCardBigView childView = mChildViews.get(i);
            addView(childView);
            childView.setQuestionVisible(false);

            final int finalI = i;
            childView.setOnClickListener(view -> {
                resetCardsLayersOrder();
                removeView(childView);
                addView(childView);

                if (mExchangeMode) {
                    boolean currentState = !mSelectionArray.get(finalI);
                    mSelectionArray.put(finalI, currentState);
                    childView.setOverlayVisible(currentState);
                    if (mOnQuestionClickListener != null) {
                        mOnQuestionClickListener.onQuestionClicked(childView.getCard());
                    }
                } else if (mQuestionsVisible) {
                    if (childView.getCard() != null && childView.getCard().getQuestion() != null) {
                        childView.setOnClickListener(view1 -> {
                            childView.setQuestionVisible(true);
                            if (childView.getCard().isActive()) {
                                childView.setOnClickListener(view3 -> {
                                    mOnQuestionClickListener.onQuestionClicked(childView.getCard());
                                });
                            } else {
                                childView.setOnClickListener(null);
                            }
                        });
                    }
                }

            });
        }
    }

    public void setOnAnimationsEndListener(OnAnimationsEndListener listener) {
        mAnimationsEndListener = listener;
    }

    public static interface OnAnimationsEndListener {
        public void onAnimationsEnd();
    }

    public List<FullGameCard> getSelectedCards() {
        List<FullGameCard> selectedCards = new ArrayList<>();
        for (int i = 0; i < mChildViews.size(); i++) {
            if (mSelectionArray.get(i)) {
                selectedCards.add(mCards.get(i));
            }
        }
        return selectedCards;
    }

    public static interface OnQuestionClickedListener {
        public void onQuestionClicked(FullGameCard card);
    }
}