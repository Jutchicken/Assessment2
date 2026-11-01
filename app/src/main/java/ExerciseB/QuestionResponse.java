package ExerciseB;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionResponse {
    @SerializedName("response_code")
    private int response_code;
    @SerializedName("results")
    private List<QuestionItem> results;

    public QuestionResponse() {
    }

    public int getResponseCode() {
        return response_code;
    }

    public List<QuestionItem> getResults() {
        return results;
    }
}