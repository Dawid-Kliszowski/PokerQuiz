package pl.pokerquiz.pokerquiz.rest;

import android.content.Context;

import java.util.List;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.Constants;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.GetQuestionsResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

public class RESTManager {
    private static final String TAG = "RESTManager";

    private static RESTManager sInstance;
    private Context mContext;
    private RESTService mRestService;

    public RESTManager(Context context) {
        mContext = context;

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST);

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("RESTManager: "));
        }

        mRestService = builder.build().create(RESTService.class);
    }

    public void getCategories(OnCategoriesDownloadedListener listener) {
        mRestService.getCategories(new Callback<List<Category>>() {
            @Override
            public void success(List<Category> categories, Response response) {
                if (listener != null) {
                    listener.onCategoriesDownloaded(true, categories);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (listener != null) {
                    listener.onCategoriesDownloaded(false, null);
                }
            }
        });
    }

    public void getQuestions(long categoryId, OnQuestionsDownloadedListener listener) {
        mRestService.getQuestions(categoryId, new Callback<GetQuestionsResponse>() {
            @Override
            public void success(GetQuestionsResponse questionResponse, Response response) {
                if (listener != null) {
                    listener.onQuestionsDownloaded(true, questionResponse.getCategories(), questionResponse.getQuestions());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (listener != null) {
                    listener.onQuestionsDownloaded(false, null, null);
                }
            }
        });
    }
}
