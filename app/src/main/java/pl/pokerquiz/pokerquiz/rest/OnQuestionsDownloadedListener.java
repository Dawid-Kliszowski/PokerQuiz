package pl.pokerquiz.pokerquiz.rest;

import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;

public interface OnQuestionsDownloadedListener {
    public void onQuestionsDownloaded(boolean success, List<Category> categories, List<QuizQuestion> questions);
}
