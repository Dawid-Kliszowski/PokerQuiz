package pl.pokerquiz.pokerquiz.gameLogic;

import android.graphics.Color;

import java.io.Serializable;

import pl.pokerquiz.pokerquiz.R;

public enum CardColor implements Serializable {
    hearts,
    diamonds,
    clubs,
    spades;

    int mImageResId;
    int mFontColor;

    static {
        hearts.mImageResId = R.drawable.heart;
        hearts.mFontColor = Color.RED;

        diamonds.mImageResId = R.drawable.diamonds;
        diamonds.mFontColor = Color.RED;

        clubs.mImageResId = R.drawable.clubs;
        clubs.mFontColor = Color.BLACK;

        spades.mImageResId = R.drawable.spades;
        spades.mFontColor = Color.BLACK;
    }
}
