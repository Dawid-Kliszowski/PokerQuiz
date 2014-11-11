package pl.pokerquiz.pokerquiz.gui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends Activity {
    private LinearLayout mLlLogoHolder;
    private LinearLayout mLllogo;
    private ImageView mImgvOverlay;

    private TextView mTxtvBottomText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();
        runAnimations();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    private void findViews() {
        mLlLogoHolder = (LinearLayout) findViewById(R.id.llLogoHolder);
        mLllogo = (LinearLayout) findViewById(R.id.llLogo);
        mImgvOverlay = (ImageView) findViewById(R.id.imgvOverlay);
        mTxtvBottomText = (TextView) findViewById(R.id.txtvBottomText);
    }

    private void runAnimations() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mImgvOverlay.setVisibility(View.VISIBLE);
            mLllogo.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mTxtvBottomText.setVisibility(View.VISIBLE);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.setDuration(600l);
                    animSet.setInterpolator(new DecelerateInterpolator(1.2f));

                    Animator translateAnimator = ObjectAnimator.ofFloat(mLlLogoHolder, "y", getResources().getDimension(R.dimen.margin_small));
                    animSet.playTogether(translateAnimator);
                    animSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {}

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (((PokerQuizApplication) getApplication()).getAppPrefs().getNickname() != null) {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(SplashActivity.this, StartupActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {}

                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                    animSet.start();

                    mTxtvBottomText.setVisibility(View.GONE);

                }, 2000l);
            }, 1200l);
        }, 1000l);
    }
}
