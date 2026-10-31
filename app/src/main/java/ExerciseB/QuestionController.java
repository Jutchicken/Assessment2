package ExerciseB;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionController implements Callback<QuestionResponse> {

    final String BASE_URL = "https://opentdb.com/";

    public QuestionResponse questions;

    public interface OnQuestionsLoadedListener {
        void onSuccess(QuestionResponse questions);
        void onError(String message);
    }

    private final OnQuestionsLoadedListener listener;

    public QuestionController(OnQuestionsLoadedListener listener) {
        this.listener = listener;
    }

    public void start() {

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        OpenTDBApi openTDBApi = retrofit.create(OpenTDBApi.class);

        Call<QuestionResponse> call =
            openTDBApi.getQuestions(10, 20, "easy", null);

        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            questions = response.body();
            if (questions.getResponseCode() == 0 && questions.getResults() != null) {
                Log.d("QUESTION_API",
                    "Getting questions successfully: "
                        + questions.getResults().size());
                listener.onSuccess(questions);
            } else {
                Log.d("QUESTION_API", "Error getting questions data");
                listener.onError(
                    "OpenTDB response code: "
                        + questions.getResponseCode()
                );
            }
        } else {
            Log.d("QUESTION_API", "API request failed");
            listener.onError("API request failed: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<QuestionResponse> call, Throwable t) {
        Log.d("QUESTION_API", "Error getting questions", t);
        listener.onError(t.getMessage());
    }
}