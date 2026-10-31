package Project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assessment2.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class A_CreateTournament extends AppCompatActivity {

    EditText etTitle;
    Spinner spCategory, spDifficulty, spType;

    FirebaseFirestore database;

    String[] categories = {
            "General Knowledge",
            "Books",
            "Film",
            "Music",
            "Video Games",
            "Science & Nature",
            "Computers",
            "Mathematics",
            "Sports",
            "Geography",
            "History",
            "Mythology"
    };

    int[] categoryIds = {
            9, 10, 11, 12, 15, 17, 18, 19, 21, 22, 23, 20
    };

    String[] difficulties = {
            "easy",
            "medium",
            "hard"
    };

    String[] types = {
            "Multiple Choice",
            "True / False"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_create_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etTitle = findViewById(R.id.et_tournamentTitle);

        spCategory = findViewById(R.id.sp_category);
        spDifficulty = findViewById(R.id.sp_difficulty);
        spType = findViewById(R.id.sp_type);

        database = FirebaseFirestore.getInstance();

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                categories
        );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                difficulties
        );

        difficultyAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDifficulty.setAdapter(difficultyAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                types
        );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spType.setAdapter(typeAdapter);

    }

    public void btnCreateTournament(View view) {
        String title = etTitle.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Please enter Tournament Title",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int categoryPosition = spCategory.getSelectedItemPosition();
        int category = categoryIds[categoryPosition];

        String difficulty = spDifficulty.getSelectedItem().toString();

        String selectedType = spType.getSelectedItem().toString();

        String type;

        if (selectedType.equals("Multiple Choice")) {
            type = "multiple";
        }
        else {
            type = "boolean";
        }

        int amount = 10;
        String status = "Open";

        String id = database.collection("tournaments").document().getId();

        TournamentItem tournament = new TournamentItem(
                id,
                title,
                category,
                difficulty,
                type,
                amount,
                status
        );

        database.collection("tournaments")
                .document(id)
                .set(tournament)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(),
                            "Tournament Created Successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    public void btnBack(View view) {
        finish();
    }
}