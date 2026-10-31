package ExerciseB;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

public class QuestionHolder extends RecyclerView.ViewHolder {

    TextView tvQuestionNumber;
    TextView tvQuestionCategory;
    TextView tvQuestionDifficulty;
    TextView tvQuestionType;
    TextView tvQuestion;
    TextView tvCorrectAnswer;

    public QuestionHolder(@NonNull View itemView) {
        super(itemView);

        tvQuestionNumber = itemView.findViewById(R.id.tv_questionNumber);
        tvQuestionCategory = itemView.findViewById(R.id.tv_questionCategory);
        tvQuestionDifficulty = itemView.findViewById(R.id.tv_questionDifficulty);
        tvQuestionType = itemView.findViewById(R.id.tv_questionType);
        tvQuestion = itemView.findViewById(R.id.tv_question);
        tvCorrectAnswer = itemView.findViewById(R.id.tv_correctAnswer);
    }
}