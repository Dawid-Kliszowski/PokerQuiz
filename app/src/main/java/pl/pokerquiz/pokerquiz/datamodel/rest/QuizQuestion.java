package pl.pokerquiz.pokerquiz.datamodel.rest;

import com.google.gson.annotations.SerializedName;

public class QuizQuestion {
    @SerializedName("id")
    private long mId;

    @SerializedName("category_id")
    private long mCategoryId;

    @SerializedName("question")
    private String mQuestion;

    @SerializedName("answer_1")
    private String mAnswerFirst;

    @SerializedName("answer_2")
    private String mAnswerSecond;

    @SerializedName("answer_3")
    private String mAnswerThird;

    @SerializedName("answer_4")
    private String mAnswerFourth;

    @SerializedName("correct_answer")
    private int mCorrectAnswer;

    public long getId() {
        return mId;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswerFirst() {
        return mAnswerFirst;
    }

    public String getAnswerSecond() {
        return mAnswerSecond;
    }

    public String getAnswerThird() {
        return mAnswerThird;
    }

    public String getAnswerFourth() {
        return mAnswerFourth;
    }

    public int getCorrectAnswer() {
        return mCorrectAnswer;
    }
}
