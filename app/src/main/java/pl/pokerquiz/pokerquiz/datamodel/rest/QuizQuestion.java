package pl.pokerquiz.pokerquiz.datamodel.rest;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

public class QuizQuestion {
    public static final String KEY_CATEGORY_ID = "category_id";
    @SerializedName("id")
    @DatabaseField(columnName = "id", id = true)
    private long mId;

    @SerializedName(KEY_CATEGORY_ID)
    @DatabaseField(columnName = KEY_CATEGORY_ID)
    private long mCategoryId;

    @SerializedName("question")
    @DatabaseField(columnName = "question")
    private String mQuestion;

    @SerializedName("answer_1")
    @DatabaseField(columnName = "answer_1")
    private String mAnswerFirst;

    @SerializedName("answer_2")
    @DatabaseField(columnName = "answer_2")
    private String mAnswerSecond;

    @SerializedName("answer_3")
    @DatabaseField(columnName = "answer_3")
    private String mAnswerThird;

    @SerializedName("answer_4")
    @DatabaseField(columnName = "answer_4")
    private String mAnswerFourth;

    @SerializedName("correct_answer")
    @DatabaseField(columnName = "correct_answer")
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
