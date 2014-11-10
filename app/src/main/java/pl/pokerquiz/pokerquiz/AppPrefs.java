package pl.pokerquiz.pokerquiz;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private static final String SHARED_PREFERENCES = "PokerQuizPrefs";
    private static final String PREF_NICKNAME = "nickname";
    private final SharedPreferences mSharedPreferences;


    public AppPrefs(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE );
    }

    public void setNickname(String nickname) {
        mSharedPreferences.edit().putString(PREF_NICKNAME, nickname).apply();
    }

    public String getNickname() {
        return mSharedPreferences.getString(PREF_NICKNAME, null);
    }
}
