package pl.pokerquiz.pokerquiz.datamodel.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetQuestionsResponse {
    @SerializedName("categories")
    List<Category> mCategories;

    @SerializedName("questions")
    List<QuizQuestion> mQuestions;

    public List<Category> getCategories() {
        return mCategories;
    }

    public List<QuizQuestion> getQuestions() {
        return mQuestions;
    }
}
