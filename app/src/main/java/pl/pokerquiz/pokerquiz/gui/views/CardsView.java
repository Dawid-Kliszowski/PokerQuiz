package pl.pokerquiz.pokerquiz.gui.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.gameCommunication.FullGameCard;

public class CardsView extends RelativeLayout {
    public static final int CARDS_COUNT = 5;

    private int mChildHeight;
    private int mChildWidth;
    private boolean mExpanded = false;
    private float mChildDimensRatio = 0.7f;
    private OnAnimationsEndListener mAnimationsEndListener;
    private List<PokerCardBigView> mChildViews = new ArrayList<>();
    private OnQuestionClickListener mOnQuestionClickListener;
    private boolean mQuestionsVisible;

    private List<FullGameCard> mCards;

    public CardsView(Context context) {
        super(context);
        init();
    }

    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (mCards != null && mCards.size() > 0) {
            fillChildViews();
        }
    }

    private void fillChildViews() {
        if (mChildViews.size() == 0) {
            for (int i = 0; i < CARDS_COUNT; i++) {
                PokerCardBigView bigCardView = new PokerCardBigView(getContext());
                mChildViews.add(bigCardView);
                addView(bigCardView);
                bigCardView.setQuestionVisible(false);

                bigCardView.setOnClickListener(view -> {
                    resetCardsLayersOrder();
                    removeView(bigCardView);
                    addView(bigCardView);
                    if (mQuestionsVisible && bigCardView.getCard() != null && bigCardView.getCard().getQuestion() != null) {
                        bigCardView.setOnClickListener(view1 -> {
                            bigCardView.setQuestionVisible(true);
                            if (bigCardView.getCard().isActive()) {
                                bigCardView.setOnClickListener(view3 -> {
                                    mOnQuestionClickListener.onQuestionClick(bigCardView.getCard());
                                });
                            } else {
                                bigCardView.setOnClickListener(null);
                            }
                        });
                    }
                });
            }
        }
    }

    public void setQuestionsVisible(boolean visible) {
        mQuestionsVisible = visible;
    }

    public void setOnQuestionClickListener(OnQuestionClickListener listener) {
        mOnQuestionClickListener = listener;
    }

    private void removeChildViews() {
        for (View childView : mChildViews) {
            removeView(childView);
        }
        mChildViews.clear();
    }

    private Rect computeChildFrame(boolean expanded, int index) {
        final int leftOffset = (int) ((float) index * (((float) getWidth() - (float) mChildWidth) / (float) (getChildCount() - 1)));

        if (expanded) {
            return new Rect(leftOffset, 0, leftOffset + mChildWidth, getHeight());
        } else {
            return new Rect(-mChildWidth, 0, 0, getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        mChildHeight = layoutHeight;
        mChildWidth = (int) ((float) mChildHeight * mChildDimensRatio);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        final int count = getChildCount();
        for (View childView : mChildViews) {
            childView.measure(MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < mChildViews.size(); i++) {
            Rect frame = computeChildFrame(mExpanded, i);

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
        Rect frame = computeChildFrame(!mExpanded, index);

        final int toXDelta = frame.left - child.getLeft();
        final int toYDelta = frame.top - child.getTop();

        Interpolator interpolator = mExpanded ? new AccelerateInterpolator() : new DecelerateInterpolator();
        final long startOffset = mExpanded ? computeStartOffset(childCount, false, childCount - index, 0.1f, duration, interpolator) :
                computeStartOffset(childCount, false, index, 0.1f, duration, interpolator);

        Animation animation = mExpanded ? createShrinkAnimation(0, toXDelta, 0, toYDelta, startOffset, duration,
                interpolator) : createExpandAnimation(0, toXDelta, 0, toYDelta, startOffset, duration, interpolator);

        final boolean isLast = getTransformedIndex(expanded, childCount, index) == childCount - 1;
        animation.setAnimationListener(new Animation.AnimationListener() {

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
                bindChildAnimation(mChildViews.get(i), i, 300);
            }
        }

        mExpanded = !mExpanded;

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

        for (PokerCardBigView childView : mChildViews) {
            addView(childView);
            childView.setQuestionVisible(false);
            childView.setOnClickListener(view -> {
                resetCardsLayersOrder();
                removeView(childView);
                addView(childView);
                if (mQuestionsVisible && childView.getCard() != null && childView.getCard().getQuestion() != null) {
                    childView.setOnClickListener(view1 -> {
                        childView.setQuestionVisible(true);
                        if (childView.getCard().isActive()) {
                            childView.setOnClickListener(view3 -> {
                                mOnQuestionClickListener.onQuestionClick(childView.getCard());
                            });
                        } else {
                            childView.setOnClickListener(null);
                        }
                    });
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

    public static interface OnQuestionClickListener {
        public void onQuestionClick(FullGameCard card);
    }
}
