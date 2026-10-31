package ExerciseB;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

import java.util.ArrayList;
import java.util.List;

public class Question extends AppCompatActivity {

    private RecyclerView rvQuestions;
    private TextView tvEmptyQuestions;

    private QuestionAdapter adapter;
    private final List<QuestionItem> questionList = new ArrayList<>();

    private QuestionController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.eb_activity_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rvQuestions = findViewById(R.id.rv_questions);
        tvEmptyQuestions = findViewById(R.id.tv_emptyQuestions);

        adapter = new QuestionAdapter(questionList);

        rvQuestions.setLayoutManager(new LinearLayoutManager(this));

        rvQuestions.setAdapter(adapter);
        controller = new QuestionController(
                new QuestionController.OnQuestionsLoadedListener() {

                    @Override
                    public void onSuccess(QuestionResponse questions) {
                        questionList.clear();
                        questionList.addAll(questions.getResults());
                        adapter.notifyDataSetChanged();
                        tvEmptyQuestions.setVisibility(
                            questionList.isEmpty() ? View.VISIBLE : View.GONE
                        );
                    }

                    @Override
                    public void onError(String message) {

                        Toast.makeText(getApplicationContext(),
                            "Error: " + message,
                            Toast.LENGTH_LONG).show();

                        tvEmptyQuestions.setText("Failed to load questions");
                        tvEmptyQuestions.setVisibility(View.VISIBLE);
                    }
                }
        );

        loadQuestions();
    }

    private void loadQuestions() {
        tvEmptyQuestions.setText("Loading questions...");
        tvEmptyQuestions.setVisibility(View.VISIBLE);

        controller.start();
    }
}