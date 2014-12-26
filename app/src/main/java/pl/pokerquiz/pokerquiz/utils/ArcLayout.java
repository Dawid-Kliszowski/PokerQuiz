package pl.pokerquiz.pokerquiz.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class ArcLayout extends ViewGroup {
    private int mChildHeight;

    private int mChildWidth;

    public static final float DEFAULT_FROM_DEGREES = 180.0f;

    public static final float DEFAULT_TO_DEGREES = 360.0f;

    private float mFromDegrees = DEFAULT_FROM_DEGREES;

    private float mToDegrees = DEFAULT_TO_DEGREES;

    private int mRadius;

    private boolean mExpanded = false;

    private float mChildDimensRatio = 0.7f;

    private int mHeightOnNotExpanded;

    public ArcLayout(Context context) {
        super(context);
        init();
    }

    public ArcLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    private void init() {
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mHeightOnNotExpanded = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mChildHeight = MeasureSpec.getSize(heightMeasureSpec) - mRadius;
        mChildWidth = (int) ((float) mChildHeight * mChildDimensRatio);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int childCount = getChildCount();
        final float perDegrees = (mToDegrees - mFromDegrees) / (childCount - 1);

        float degrees = mFromDegrees;

        for (int i = 0; i < childCount; i++) {
            Rect frame = computeChildFrame(mExpanded, i, degrees);
            degrees += perDegrees;

            getChildAt(i).layout(frame.left, frame.top, frame.right, frame.bottom);
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

    public void setChildDimensRatio(float ratio) {

    }

    public void switchState(final boolean showAnimation) {
        if (showAnimation) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                bindChildAnimation(getChildAt(i), i, 240);
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
    }

}