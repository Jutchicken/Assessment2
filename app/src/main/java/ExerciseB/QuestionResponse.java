package ExerciseB;

import java.util.List;

public class QuestionResponse {

    private int response_code;
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