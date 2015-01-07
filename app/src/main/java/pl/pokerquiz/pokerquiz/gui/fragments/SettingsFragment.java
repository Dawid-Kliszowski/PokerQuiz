package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.utils.LocaleManager;

public class SettingsFragment extends Fragment {
    private View mRootView;
    private TextView mTxtvSelectLanguage;
    private RadioButton mRbSystemLanguage;
    private RadioButton mRbEnglish;
    private RadioButton mRbPolish;

    private LocaleManager mLocaleManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment needs layoutInflater from Activity because of Lollipop broken inflation
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, container, false);

        mLocaleManager = LocaleManager.getInstance(getActivity());
        findViews();
        setViews();
        setListeners();

        return mRootView;
    }

    private void findViews() {
        mTxtvSelectLanguage = (TextView) mRootView.findViewById(R.id.txtvSelectLanguage);
        mRbSystemLanguage = (RadioButton) mRootView.findViewById(R.id.rbSystemLanguage);
        mRbEnglish = (RadioButton) mRootView.findViewById(R.id.rbEnglish);
        mRbPolish = (RadioButton) mRootView.findViewById(R.id.rbPolish);
    }

    private void setViews() {
        LocaleManager.AvailableLocale currentLocale = mLocaleManager.getCurrentLocale();

        switch (currentLocale) {
            case SystemLanguage:
                mRbSystemLanguage.setChecked(true);
                break;
            case English:
                mRbEnglish.setChecked(true);
                break;
            case Polish:
                mRbPolish.setChecked(true);
                break;
        }

        Resources localizedRes = mLocaleManager.getLocalizedResources();
        mTxtvSelectLanguage.setText(localizedRes.getString(R.string.select_language));
        mRbSystemLanguage.setText(localizedRes.getString(R.string.system_language));
        mRbEnglish.setText(localizedRes.getString(R.string.english));
        mRbPolish.setText(localizedRes.getString(R.string.polish));
    }

    private void setListeners() {
        mRbSystemLanguage.setOnClickListener(view -> {
            mLocaleManager.setCurrentLocale(LocaleManager.AvailableLocale.SystemLanguage);
            setViews();
        });

        mRbEnglish.setOnClickListener(view -> {
            mLocaleManager.setCurrentLocale(LocaleManager.AvailableLocale.English);
            setViews();
        });

        mRbPolish.setOnClickListener(view -> {
            mLocaleManager.setCurrentLocale(LocaleManager.AvailableLocale.Polish);
            setViews();
        });
    }
}
