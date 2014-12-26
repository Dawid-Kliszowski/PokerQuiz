package pl.pokerquiz.pokerquiz.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {
    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // get top left coordinates
        int left = child.getLeft();
        int top = child.getTop();

        // get offset to center
        int centerX = child.getWidth() / 2;
        int centerY = child.getHeight() / 2;

        // get absolute center of child
        float pivotX = left + centerX;
        float pivotY = top + centerY;

        // calculate distance from center
        float centerScreen = getHeight() / 2;
        float distFromCenter = (pivotY - centerScreen) / centerScreen;

        // calculate scale and rotation
        float scale = (float)(1 - 0.5 * (1 - Math.cos(distFromCenter)));
        float rotation = 30 * distFromCenter;

        canvas.save();
        canvas.rotate(rotation, pivotX, pivotY);
        canvas.scale(scale, scale, pivotX, pivotY);
        super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return false;
    }
}
