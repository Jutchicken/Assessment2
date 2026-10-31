package ExerciseB;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> {

    private final List<QuestionItem> items;

    public QuestionAdapter(List<QuestionItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eb_question_item, parent, false);

        return new QuestionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHolder holder, int position
    ) {
        QuestionItem question = items.get(position);

        holder.tvQuestionNumber.setText("Question " + (position + 1));

        holder.tvQuestionCategory.setText("Category: " + decodeHtml(question.getCategory()));

        holder.tvQuestionDifficulty.setText("Difficulty: " + question.getDifficulty());

        holder.tvQuestionType.setText("Type: " + question.getType());

        holder.tvQuestion.setText(decodeHtml(question.getQuestion()));

        holder.tvCorrectAnswer.setText("Correct Answer: " + decodeHtml(question.getCorrectAnswer()));
    }

    private String decodeHtml(String text) {
        if (text == null) {
            return "";
        }

        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}