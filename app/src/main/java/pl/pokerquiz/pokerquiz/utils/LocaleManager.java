package pl.pokerquiz.pokerquiz.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pl.pokerquiz.pokerquiz.AppPrefs;
import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;

public class LocaleManager {
    private static LocaleManager sInstance;
    private Resources mCurrentResources;
    private Context mContext;

    public static LocaleManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocaleManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public LocaleManager(Context context) {
        mContext = context;
        String prefLocale = PokerQuizApplication.getAppPrefs().getCurrentLocale();
        Resources standardResources = context.getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());

        if (prefLocale == null) {
            mCurrentResources = context.getResources();
        } else {
            Locale locale;
            try {
                locale = AvailableLocale.valueOf(prefLocale).getLocale();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
                locale = AvailableLocale.DEFAULT_LOCALE.getLocale();

            }
            config.locale = locale;
            mCurrentResources = new Resources(assets, metrics, config);
        }
    }

    public Resources getLocalizedResources() {
        return mCurrentResources;
    }

    public void setCurrentLocale(AvailableLocale locale) {
        AssetManager assets = mCurrentResources.getAssets();
        DisplayMetrics metrics = mCurrentResources.getDisplayMetrics();
        Configuration config = new Configuration(mCurrentResources.getConfiguration());

        if (locale.getLocale() != null) {
            PokerQuizApplication.getAppPrefs().setCurrentLocale(locale.getPrefsValue());

            config.locale = locale.getLocale();
            mCurrentResources = new Resources(assets, metrics, config);
        } else {
            PokerQuizApplication.getAppPrefs().setCurrentLocale(null);
            config.locale = AvailableLocale.English.getLocale();
            new Resources(assets, metrics, config);
            mCurrentResources = mContext.getResources();
        }
    }

    public AvailableLocale getCurrentLocale() {
        String prefLocale = PokerQuizApplication.getAppPrefs().getCurrentLocale();
        if (prefLocale == null) {
            return AvailableLocale.SystemLanguage;
        } else {
            AvailableLocale locale;
            try {
                locale = AvailableLocale.valueOf(prefLocale);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
                locale = AvailableLocale.DEFAULT_LOCALE;
            }
            return locale;
        }
    }

    public static enum AvailableLocale {
        SystemLanguage,
        English,
        Polish;

        private static final AvailableLocale DEFAULT_LOCALE = AvailableLocale.English;
        private static final HashMap<AvailableLocale, Locale> sEnumLocaleMap;
        private static final List<AvailableLocale> mOrderedLocales;

        static {
            sEnumLocaleMap = new HashMap<AvailableLocale, Locale>();
            sEnumLocaleMap.put(English, Locale.US);
            sEnumLocaleMap.put(Polish, new Locale("pl", "PL"));

            mOrderedLocales = new ArrayList<AvailableLocale>();
            mOrderedLocales.add(SystemLanguage);
            mOrderedLocales.add(English);
            mOrderedLocales.add(Polish);
        }

        public static List<AvailableLocale> getOrderedLocales() {
            return mOrderedLocales;
        }

        public String getPrefsValue() {
            if (this.equals(SystemLanguage)) {
                return null;
            } else {
                return name();
            }
        }

        public Locale getLocale() {
            return sEnumLocaleMap.get(this);
        }
    }
}
