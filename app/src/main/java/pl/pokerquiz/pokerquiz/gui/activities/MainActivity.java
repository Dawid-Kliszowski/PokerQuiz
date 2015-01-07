package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gui.fragments.HomeFragment;
import pl.pokerquiz.pokerquiz.gui.fragments.MainMenuFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {
    private ImageView mImgvMenuButton;
    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSlidingMenu();
        findViews();
        setListeners();

        setFragment(new HomeFragment(), true);
    }

        @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    private void findViews() {
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
        setMenuFragment(new MainMenuFragment());
    }

    public void setFragment(Fragment fragment, boolean clearBackStack) {
        if (clearBackStack) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragmentContainer, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    private void setMenuFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.flMenuContainer, fragment, "fragment");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
