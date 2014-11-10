package pl.pokerquiz.pokerquiz.gui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import pl.pokerquiz.pokerquiz.Constans;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class StartupActivity extends Activity {
    private EditText mEtNickname;
    private Button mBtnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        findViews();
        setListeners();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    private void findViews() {
        mEtNickname = (EditText) findViewById(R.id.etNickname);
        mBtnSubmit = (Button) findViewById(R.id.btnSubmit);
    }

    private void setListeners() {
        mBtnSubmit.setOnClickListener(view -> {
            String nickname = mEtNickname.getText().toString().trim();
            if (isNicknameValid(nickname)) {
                ((PokerQuizApplication) getApplication()).getAppPrefs().setNickname(nickname);
                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isNicknameValid(String nickname) {
        return nickname.length() > Constans.MINIMUM_NICKNAME_LENGTH && nickname.length() < Constans.MAXIMUM_NICKNAME_LENGTH;
    }
}
