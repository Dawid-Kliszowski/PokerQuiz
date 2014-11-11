package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gui.fragments.HomeFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {
    private FrameLayout mFlFragmentContainer;
    private ImageView mImgvMenuButton;
    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setListeners();
        initSlidingMenu();

        setFragment(new HomeFragment());
    }

        @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    private void findViews() {
        mFlFragmentContainer = (FrameLayout) findViewById(R.id.flFragmentContainer);
        mImgvMenuButton = (ImageView) findViewById(R.id.imgvMenuButton);
    }

    private void setListeners() {
        mImgvMenuButton.setOnClickListener(view -> {
            if (mSlidingMenu.isMenuShowing()) {
                mSlidingMenu.showContent();
            } else {
                mSlidingMenu.showMenu();
            }
        });
    }

    private void initSlidingMenu() {
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_left_shadow);
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
        mSlidingMenu.setFadeDegree(0.6f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.menu_main);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.flFragmentContainer, fragment, "fragment");
        transaction.commit();
    }
}
