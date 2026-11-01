package Project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class P_MyResults extends AppCompatActivity {

    private RecyclerView rvResults;
    private TextView tvResultsStatus;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private final List<ResultItem> items = new ArrayList<>();
    private ResultAdapter adapter;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_my_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvResults = findViewById(R.id.rvResults);
        tvResultsStatus = findViewById(R.id.tvResultsStatus);
        btnBack = findViewById(R.id.btnBackResults);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        adapter = new ResultAdapter(items);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        loadResults();
    }

    private void loadResults() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            tvResultsStatus.setText("Please login first");
            return;
        }

        tvResultsStatus.setText("Loading results...");

        db.collection("results")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    items.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document : snapshot.getDocuments()) {
                        ResultItem result = document.toObject(ResultItem.class);

                        if (result != null) {
                            items.add(result);
                        }
                    }

                    items.sort((a, b) -> {
                        if (a.getCompletedAt() == null) {
                            return 1;
                        }
                        if (b.getCompletedAt() == null) {
                            return -1;
                        }
                        return b.getCompletedAt().compareTo(a.getCompletedAt());
                    });

                    adapter.notifyDataSetChanged();

                    if (items.isEmpty()) {
                        tvResultsStatus.setText("No completed tournaments yet");
                    }
                    else {
                        tvResultsStatus.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                            tvResultsStatus.setText("Failed to load results: " + e.getMessage());
                });
    }
}