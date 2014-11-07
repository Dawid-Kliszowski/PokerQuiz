package pl.pokerquiz.pokerquiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class StartupActivity extends Activity {
    private EditText mEtNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    private void findViews() {
        mEtNickname = (EditText) findViewById(R.id.etNickname);
    }

    private void setListeners() {

    }


}
