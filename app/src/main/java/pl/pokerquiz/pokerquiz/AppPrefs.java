package pl.pokerquiz.pokerquiz;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private static final String SHARED_PREFERENCES = "PokerQuizPrefs";

    private static final String PREF_NICKNAME = "nickname";
    private static final String PREF_AVATAR_BASE64 = "avatar_base64";
    private static final String PREF_CURRENT_LOCALE = "current_locale";

    private final SharedPreferences mSharedPreferences;


    public AppPrefs(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setNickname(String nickname) {
        mSharedPreferences.edit().putString(PREF_NICKNAME, nickname).apply();
    }
    public String getNickname() {
        return mSharedPreferences.getString(PREF_NICKNAME, null);
    }

    public void setAvatarBase64(String avatarBase64) {
        mSharedPreferences.edit().putString(PREF_AVATAR_BASE64, avatarBase64).apply();
    }
    public String getAvatarBase64() {
        return mSharedPreferences.getString(PREF_AVATAR_BASE64, null);
    }

    public void setCurrentLocale(String locale) {
        mSharedPreferences.edit().putString(PREF_CURRENT_LOCALE, locale).apply();
    }

    public String getCurrentLocale() {
        return mSharedPreferences.getString(PREF_CURRENT_LOCALE, null);
    }
}
