package pl.pokerquiz.pokerquiz.rest;

import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.GetQuestionsResponse;
import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

public interface RESTService {

    @POST("/get_categories.php")
    public void getCategories(Callback<List<Category>> callback);

    @POST("/get_questions.php")
    public void getQuestions(@Query("category_id") long category_id,
                             Callback<GetQuestionsResponse> callback);
}
