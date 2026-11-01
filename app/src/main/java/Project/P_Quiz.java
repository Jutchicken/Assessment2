package Project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assessment2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ExerciseB.OpenTDBApi;
import ExerciseB.QuestionItem;
import ExerciseB.QuestionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class P_Quiz extends AppCompatActivity {

    TextView tvTitle, tvProgress, tvQuestion, tvScore;
    LinearLayout layoutAnswers;
    Button btnNext, btnExit;

    List<QuestionItem> questions = new ArrayList<>();
    List<String> answers = new ArrayList<>();

    int currentQuestion = 0;
    int score = 0;

    String tournamentId, tournamentTitle, difficulty, type;

    int category, amount;

    String selectedAnswer = null;

    boolean answered = false;

    OpenTDBApi api;

    FirebaseAuth auth;
    FirebaseFirestore database;

    boolean resultSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tv_quizTitle);
        tvProgress = findViewById(R.id.tv_progress);
        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);

        layoutAnswers = findViewById(R.id.layout_answers);
        btnNext = findViewById(R.id.btn_next);
        btnExit = findViewById(R.id.btn_exit);

        tournamentId = getIntent().getStringExtra("tournamentId");
        tournamentTitle = getIntent().getStringExtra("title");
        category = getIntent().getIntExtra("category", 9);
        difficulty = getIntent().getStringExtra("difficulty");
        type = getIntent().getStringExtra("type");
        amount = getIntent().getIntExtra("amount", 10);

        if (tournamentId == null || difficulty == null || type == null || amount <= 0) {
            Toast.makeText(getApplicationContext(),
                    "Invalid tournament",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(tournamentTitle != null ? tournamentTitle : "Quiz Tournament");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(OpenTDBApi.class);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        loadQuestions();
    }

    private void loadQuestions() {
        tvQuestion.setText("Loading questions...");
        btnNext.setVisibility(View.GONE);

        android.util.Log.d("QUIZ_DEBUG", "amount=" + amount + ", category=" + category
                + ", difficulty=" + difficulty + ", type=" + type
        );

        api.getQuestions(amount, category, difficulty, type)
                .enqueue(new Callback<QuestionResponse>() {
                    @Override
                    public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                        if (!response.isSuccessful()) {
                            showError("HTTP Error: " + response.code());
                            return;
                        }

                        if (response.body() == null) {
                            showError("Empty API response");
                            return;
                        }

                        QuestionResponse data = response.body();
                        android.util.Log.d("QUIZ_DEBUG", "Response Code: " + data.getResponseCode());

                        if (data.getResponseCode() != 0) {
                            if (data.getResponseCode() == 1) {
                                showError(
                                        "Not enough questions for this "
                                                + "category, difficulty and type. "
                                                + "Please choose another tournament."
                                );
                            }
                            else {
                                showError("OpenTDB Error: " + data.getResponseCode());
                            }
                            return;
                        }

                        if (data.getResults() == null || data.getResults().isEmpty()) {
                            showError("No questions returned");
                            return;
                        }

                        if (data.getResults().size() != 10) {
                            showError("Expected 10 questions, received " + data.getResults().size());
                            return;
                        }

                        questions.clear();
                        questions.addAll(data.getResults());
                        currentQuestion = 0;
                        score = 0;

                        showQuestion();
                    }

                    @Override
                    public void onFailure(Call<QuestionResponse> call, Throwable t) {
                        android.util.Log.e("QUIZ_DEBUG", "Request failed", t);
                        showError(t.getMessage());
                    }
                });
    }


    private void showQuestion() {
        if (currentQuestion >= questions.size()) {
            finishQuiz();
            return;
        }

        QuestionItem question = questions.get(currentQuestion);
        answered = false;
        selectedAnswer = null;
        tvProgress.setText("Question " + (currentQuestion + 1) + " / " + questions.size());
        tvScore.setText("Score: " + score);

        tvQuestion.setText(Html.fromHtml(
                question.getQuestion(),
                Html.FROM_HTML_MODE_LEGACY)
        );
        layoutAnswers.removeAllViews();
        answers.clear();
        answers.add(question.getCorrectAnswer());
        answers.addAll(question.getIncorrectAnswers());

        Collections.shuffle(answers);

        for (String answer : answers) {
            Button button = new Button(this);
            button.setText(Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY));
            button.setAllCaps(false);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 12);
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                if (!answered) {
                    selectedAnswer = answer;
                    checkAnswer();
                }
            });
            layoutAnswers.addView(button);
        }
        btnNext.setVisibility(View.GONE);
    }

    private void checkAnswer() {
        QuestionItem question = questions.get(currentQuestion);
        answered = true;

        if (selectedAnswer.equals(question.getCorrectAnswer())) {
            score++;
            Toast.makeText(getApplicationContext(),
                    "Correct!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Wrong! Correct answer: " + Html.fromHtml(question.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY),
                    Toast.LENGTH_LONG).show();
        }

        tvScore.setText("Score: " + score);

        for (int i = 0; i < layoutAnswers.getChildCount(); i++) {
            layoutAnswers.getChildAt(i).setEnabled(false);
        }

        btnNext.setText(currentQuestion == questions.size() - 1 ? "Finish" : "Next Question");
        btnNext.setVisibility(View.VISIBLE);
    }

    public void btnNext(View view) {
        if (!answered) {
            return;
        }
        currentQuestion++;
        showQuestion();
    }

    private void finishQuiz() {
        layoutAnswers.removeAllViews();
        tvProgress.setText("Quiz Completed");
        tvQuestion.setText("Final Score: " + score + " / " + questions.size());
        int percentage = score * 100 / questions.size();
        tvScore.setText("Percentage: " + percentage + "%");
        btnNext.setVisibility(View.GONE);
        btnExit.setVisibility(View.VISIBLE);
        saveResult(percentage);
    }

    private void saveResult(int percentage) {
        if (resultSaved) {
            return;
        }
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(getApplicationContext(),
                    "User not logged in",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        resultSaved = true;
        ResultItem result = new ResultItem(
                user.getUid(),
                tournamentId,
                tournamentTitle,
                score,
                questions.size(),
                percentage,
                new Timestamp(new Date())
        );

        database.collection("results")
                .add(result)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(),
                            "Result Saved Successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    resultSaved = false;
                    Toast.makeText(getApplicationContext(),
                            "Failed to save result: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }


    private void showError(String message) {
        tvQuestion.setText(message != null ? message : "Unable to load quiz");

        Toast.makeText(getApplicationContext(),
                "Quiz Error: " + message,
                Toast.LENGTH_LONG).show();

        android.util.Log.e("QUIZ_DEBUG", String.valueOf(message));
    }

    public void btnExit(View view) {
        Intent intent = new Intent(getApplicationContext(),
                P_MainPage.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        finish();
    }
}

