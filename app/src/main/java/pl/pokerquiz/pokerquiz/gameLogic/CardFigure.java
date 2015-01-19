package pl.pokerquiz.pokerquiz.gameLogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum CardFigure implements Serializable {
    two,
    three,
    four,
    five,
    six,
    seven,
    eight,
    nine,
    ten,
    jack,
    queen,
    king,
    ace;

    private String mSign;
    private List<IconPlaceParam> mIconPlaces = new ArrayList<>();

    static {
        two.mSign = "2";
        two.mIconPlaces.add(new IconPlaceParam(0.1f, 0.3f, 0.28f));
        two.mIconPlaces.add(new IconPlaceParam(0.6f, 0.3f, 0.28f));

        three.mSign = "3";
        three.mIconPlaces.add(new IconPlaceParam(0.05f, 0.3f, 0.28f));
        three.mIconPlaces.add(new IconPlaceParam(0.35f, 0.3f, 0.28f));
        three.mIconPlaces.add(new IconPlaceParam(0.7f, 0.3f, 0.28f));

        four.mSign = "4";
        four.mIconPlaces.add(new IconPlaceParam(0.1f, 0.05f, 0.28f));
        four.mIconPlaces.add(new IconPlaceParam(0.1f, 0.55f, 0.28f));
        four.mIconPlaces.add(new IconPlaceParam(0.6f, 0.05f, 0.28f));
        four.mIconPlaces.add(new IconPlaceParam(0.6f, 0.55f, 0.28f));

        five.mSign = "5";
        five.mIconPlaces.add(new IconPlaceParam(0.05f, 0.05f, 0.25f));
        five.mIconPlaces.add(new IconPlaceParam(0.05f, 0.55f, 0.25f));
        five.mIconPlaces.add(new IconPlaceParam(0.35f, 0.3f, 0.25f));
        five.mIconPlaces.add(new IconPlaceParam(0.7f, 0.05f, 0.25f));
        five.mIconPlaces.add(new IconPlaceParam(0.7f, 0.55f, 0.25f));

        six.mSign = "6";
        six.mIconPlaces.add(new IconPlaceParam(0.05f, 0.05f, 0.28f));
        six.mIconPlaces.add(new IconPlaceParam(0.35f, 0.05f, 0.28f));
        six.mIconPlaces.add(new IconPlaceParam(0.7f, 0.05f, 0.28f));
        six.mIconPlaces.add(new IconPlaceParam(0.05f, 0.55f, 0.28f));
        six.mIconPlaces.add(new IconPlaceParam(0.35f, 0.55f, 0.28f));
        six.mIconPlaces.add(new IconPlaceParam(0.7f, 0.55f, 0.28f));

        seven.mSign = "7";
        seven.mIconPlaces.add(new IconPlaceParam(0.05f, 0.05f, 0.22f));
        seven.mIconPlaces.add(new IconPlaceParam(0.35f, 0.05f, 0.22f));
        seven.mIconPlaces.add(new IconPlaceParam(0.7f, 0.05f, 0.22f));
        seven.mIconPlaces.add(new IconPlaceParam(0.05f, 0.62f, 0.22f));
        seven.mIconPlaces.add(new IconPlaceParam(0.35f, 0.62f, 0.22f));
        seven.mIconPlaces.add(new IconPlaceParam(0.7f, 0.62f, 0.22f));
        seven.mIconPlaces.add(new IconPlaceParam(0.2f, 0.33f, 0.22f));

        eight.mSign = "8";
        eight.mIconPlaces.add(new IconPlaceParam(0.05f, 0.05f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.35f, 0.05f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.7f, 0.05f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.05f, 0.62f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.35f, 0.62f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.7f, 0.62f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.2f, 0.33f, 0.22f));
        eight.mIconPlaces.add(new IconPlaceParam(0.52f, 0.33f, 0.22f));

        nine.mSign = "9";
        nine.mIconPlaces.add(new IconPlaceParam(0.03f, 0.03f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.27f, 0.03f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.51f, 0.03f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.75f, 0.03f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.03f, 0.63f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.27f, 0.63f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.51f, 0.63f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.75f, 0.63f, 0.22f));
        nine.mIconPlaces.add(new IconPlaceParam(0.4f, 0.35f, 0.22f));

        ten.mSign = "10";
        ten.mIconPlaces.add(new IconPlaceParam(0.03f, 0.03f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.27f, 0.03f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.51f, 0.03f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.75f, 0.03f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.03f, 0.63f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.27f, 0.63f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.51f, 0.63f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.75f, 0.63f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.15f, 0.35f, 0.22f));
        ten.mIconPlaces.add(new IconPlaceParam(0.62f, 0.35f, 0.22f));

        jack.mSign = "J";
        jack.mIconPlaces.add(new IconPlaceParam(0f, 0f, 1f));

        queen.mSign = "Q";
        queen.mIconPlaces.add(new IconPlaceParam(0f, 0f, 1f));

        king.mSign = "K";
        king.mIconPlaces.add(new IconPlaceParam(0f, 0f, 1f));

        ace.mSign = "A";
        ace.mIconPlaces.add(new IconPlaceParam(0.25f, 0.1f, 0.5f));
    }

    String getSign() {
        return mSign;
    }

    List<IconPlaceParam> getIconPlaces() {
        return mIconPlaces;
    }

    public static class IconPlaceParam {
        private float mTop;
        private float mLeft;
        private float mSize;

        public IconPlaceParam(float top, float left, float size) {
            mTop = top;
            mLeft = left;
            mSize = size;
        }

        public float getTop() {
            return mTop;
        }

        public float getLeft() {
            return mLeft;
        }

        public float getSize() {
            return mSize;
        }
    }
}
