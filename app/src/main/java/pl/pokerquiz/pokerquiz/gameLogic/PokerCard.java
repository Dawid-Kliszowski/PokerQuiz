package pl.pokerquiz.pokerquiz.gameLogic;

public enum PokerCard {
    hearts_two,
    hearts_three,
    hearts_four,
    hearts_five,
    hearts_six,
    hearts_seven,
    hearts_eight,
    hearts_nine,
    hearts_ten,
    hearts_jack,
    hearts_queen,
    hearts_king,
    hearts_ace,

    diamonds_two,
    diamonds_three,
    diamonds_four,
    diamonds_five,
    diamonds_six,
    diamonds_seven,
    diamonds_eight,
    diamonds_nine,
    diamonds_ten,
    diamonds_jack,
    diamonds_queen,
    diamonds_king,
    diamonds_ace,

    clubs_two,
    clubs_three,
    clubs_four,
    clubs_five,
    clubs_six,
    clubs_seven,
    clubs_eight,
    clubs_nine,
    clubs_ten,
    clubs_jack,
    clubs_queen,
    clubs_king,
    clubs_ace,

    spades_two,
    spades_three,
    spades_four,
    spades_five,
    spades_six,
    spades_seven,
    spades_eight,
    spades_nine,
    spades_ten,
    spades_jack,
    spades_queen,
    spades_king,
    spades_ace;

    private CardColor mColor;
    private CardFigure mFigure;


    static {
        hearts_two.mColor = CardColor.hearts;
        hearts_two.mFigure = CardFigure.two;

        hearts_three.mColor = CardColor.hearts;
        hearts_three.mFigure = CardFigure.three;

        hearts_four.mColor = CardColor.hearts;
        hearts_four.mFigure = CardFigure.four;

        hearts_five.mColor = CardColor.hearts;
        hearts_five.mFigure = CardFigure.five;

        hearts_six.mColor = CardColor.hearts;
        hearts_six.mFigure = CardFigure.six;

        hearts_seven.mColor = CardColor.hearts;
        hearts_seven.mFigure = CardFigure.seven;

        hearts_eight.mColor = CardColor.hearts;
        hearts_eight.mFigure = CardFigure.eight;

        hearts_nine.mColor = CardColor.hearts;
        hearts_nine.mFigure = CardFigure.nine;

        hearts_ten.mColor = CardColor.hearts;
        hearts_ten.mFigure = CardFigure.ten;

        hearts_jack.mColor = CardColor.hearts;
        hearts_jack.mFigure = CardFigure.jack;

        hearts_queen.mColor = CardColor.hearts;
        hearts_queen.mFigure = CardFigure.queen;

        hearts_king.mColor = CardColor.hearts;
        hearts_king.mFigure = CardFigure.king;

        hearts_ace.mColor = CardColor.hearts;
        hearts_ace.mFigure = CardFigure.ace;

////////////////////////////////////////////////////////////////////////

        diamonds_two.mColor = CardColor.diamonds;
        diamonds_two.mFigure = CardFigure.two;

        diamonds_three.mColor = CardColor.diamonds;
        diamonds_three.mFigure = CardFigure.three;

        diamonds_four.mColor = CardColor.diamonds;
        diamonds_four.mFigure = CardFigure.four;

        diamonds_five.mColor = CardColor.diamonds;
        diamonds_five.mFigure = CardFigure.five;

        diamonds_six.mColor = CardColor.diamonds;
        diamonds_six.mFigure = CardFigure.six;

        diamonds_seven.mColor = CardColor.diamonds;
        diamonds_seven.mFigure = CardFigure.seven;

        diamonds_eight.mColor = CardColor.diamonds;
        diamonds_eight.mFigure = CardFigure.eight;

        diamonds_nine.mColor = CardColor.diamonds;
        diamonds_nine.mFigure = CardFigure.nine;

        diamonds_ten.mColor = CardColor.diamonds;
        diamonds_ten.mFigure = CardFigure.ten;

        diamonds_jack.mColor = CardColor.diamonds;
        diamonds_jack.mFigure = CardFigure.jack;

        diamonds_queen.mColor = CardColor.diamonds;
        diamonds_queen.mFigure = CardFigure.queen;

        diamonds_king.mColor = CardColor.diamonds;
        diamonds_king.mFigure = CardFigure.king;

        diamonds_ace.mColor = CardColor.diamonds;
        diamonds_ace.mFigure = CardFigure.ace;

////////////////////////////////////////////////////////////////////////

        clubs_two.mColor = CardColor.clubs;
        clubs_two.mFigure = CardFigure.two;

        clubs_three.mColor = CardColor.clubs;
        clubs_three.mFigure = CardFigure.three;

        clubs_four.mColor = CardColor.clubs;
        clubs_four.mFigure = CardFigure.four;

        clubs_five.mColor = CardColor.clubs;
        clubs_five.mFigure = CardFigure.five;

        clubs_six.mColor = CardColor.clubs;
        clubs_six.mFigure = CardFigure.six;

        clubs_seven.mColor = CardColor.clubs;
        clubs_seven.mFigure = CardFigure.seven;

        clubs_eight.mColor = CardColor.clubs;
        clubs_eight.mFigure = CardFigure.eight;

        clubs_nine.mColor = CardColor.clubs;
        clubs_nine.mFigure = CardFigure.nine;

        clubs_ten.mColor = CardColor.clubs;
        clubs_ten.mFigure = CardFigure.ten;

        clubs_jack.mColor = CardColor.clubs;
        clubs_jack.mFigure = CardFigure.jack;

        clubs_queen.mColor = CardColor.clubs;
        clubs_queen.mFigure = CardFigure.queen;

        clubs_king.mColor = CardColor.clubs;
        clubs_king.mFigure = CardFigure.king;

        clubs_ace.mColor = CardColor.clubs;
        clubs_ace.mFigure = CardFigure.ace;

////////////////////////////////////////////////////////////////////////

        spades_two.mColor = CardColor.spades;
        spades_two.mFigure = CardFigure.two;

        spades_three.mColor = CardColor.spades;
        spades_three.mFigure = CardFigure.three;

        spades_four.mColor = CardColor.spades;
        spades_four.mFigure = CardFigure.four;

        spades_five.mColor = CardColor.spades;
        spades_five.mFigure = CardFigure.five;

        spades_six.mColor = CardColor.spades;
        spades_six.mFigure = CardFigure.six;

        spades_seven.mColor = CardColor.spades;
        spades_seven.mFigure = CardFigure.seven;

        spades_eight.mColor = CardColor.spades;
        spades_eight.mFigure = CardFigure.eight;

        spades_nine.mColor = CardColor.spades;
        spades_nine.mFigure = CardFigure.nine;

        spades_ten.mColor = CardColor.spades;
        spades_ten.mFigure = CardFigure.ten;

        spades_jack.mColor = CardColor.spades;
        spades_jack.mFigure = CardFigure.jack;

        spades_queen.mColor = CardColor.spades;
        spades_queen.mFigure = CardFigure.queen;

        spades_king.mColor = CardColor.spades;
        spades_king.mFigure = CardFigure.king;

        spades_ace.mColor = CardColor.spades;
        spades_ace.mFigure = CardFigure.ace;
    }
}
