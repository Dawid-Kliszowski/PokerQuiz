package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.gui.fragments.HomeFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {
    private FrameLayout mFlFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        setFragment(new HomeFragment());
    }

        @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    private void findViews() {
        mFlFragmentContainer = (FrameLayout) findViewById(R.id.flFragmentContainer);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.flFragmentContainer, fragment, "fragment");
        transaction.commit();
    }
}
