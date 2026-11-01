package Project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class P_Leaderboard extends AppCompatActivity {

    private Spinner spTournament;
    private TextView tvLeaderboardStatus;
    private RecyclerView rvLeaderboard;
    private FirebaseFirestore db;
    private final List<TournamentItem> tournaments = new ArrayList<>();
    private final List<LeaderboardItem> leaderboardItems = new ArrayList<>();
    private LeaderboardAdapter adapter;
    private int requestVersion = 0;
    Button btnBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        spTournament = findViewById(R.id.spTournament);
        tvLeaderboardStatus = findViewById(R.id.tvLeaderboardStatus);
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        btnBack = findViewById(R.id.btnBackLeaderboard);
        db = FirebaseFirestore.getInstance();
        adapter = new LeaderboardAdapter(leaderboardItems);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        rvLeaderboard.setAdapter(adapter);
        btnBack.setOnClickListener(v -> finish());
        loadTournaments();
    }

    private void loadTournaments() {
        tvLeaderboardStatus.setText("Loading tournaments...");
        db.collection("tournaments")
                .get()
                .addOnSuccessListener(snapshot -> {
                    tournaments.clear();
                    List<String> titles = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        TournamentItem tournament = document.toObject(TournamentItem.class);

                        if (tournament == null) {
                            continue;
                        }

                        tournament.setId(document.getId());
                        tournaments.add(tournament);
                        titles.add(tournament.getTitle());
                    }

                    if (tournaments.isEmpty()) {
                        tvLeaderboardStatus.setText("No tournaments available");
                        return;
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titles);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTournament.setAdapter(spinnerAdapter);
                    spTournament.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    loadLeaderboard(tournaments.get(position).getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            }
                    );

                }).addOnFailureListener(e -> {
                    tvLeaderboardStatus.setText("Failed to load tournaments: " + e.getMessage());
                });
    }

    private void loadLeaderboard(String tournamentId) {
        final int currentRequest = ++requestVersion;
        leaderboardItems.clear();
        adapter.notifyDataSetChanged();
        tvLeaderboardStatus.setVisibility(View.VISIBLE);
        tvLeaderboardStatus.setText("Loading leaderboard...");

        db.collection("results")
                .whereEqualTo("tournamentId", tournamentId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (currentRequest != requestVersion) {
                        return;
                    }
                    if (snapshot.isEmpty()) {
                        tvLeaderboardStatus.setText("No results yet");
                        return;
                    }

                    List<LeaderboardItem> loadedItems = new ArrayList<>();
                    for (DocumentSnapshot result : snapshot.getDocuments()) {
                        String userId = result.getString("userId");
                        Long scoreValue = result.getLong("score");
                        Long totalValue = result.getLong("totalQuestions");

                        int score = scoreValue == null ? 0 : scoreValue.intValue();

                        int total = totalValue == null ? 0 : totalValue.intValue();

                        Timestamp completedAt = result.getTimestamp("completedAt");

                        loadedItems.add(
                                new LeaderboardItem(
                                        userId,
                                        "Player",
                                        score,
                                        total,
                                        completedAt
                                )
                        );
                    }

                    loadedItems.sort((a, b) -> {
                        int scoreCompare = Integer.compare(b.getScore(), a.getScore());

                        if (scoreCompare != 0) {
                            return scoreCompare;
                        }

                        Timestamp timeA = a.getCompletedAt();
                        Timestamp timeB = b.getCompletedAt();
                        if (timeA == null && timeB == null) return 0;
                        if (timeA == null) return 1;
                        if (timeB == null) return -1;

                        return timeA.compareTo(timeB);
                    });

                    leaderboardItems.clear();
                    leaderboardItems.addAll(loadedItems);
                    adapter.notifyDataSetChanged();

                    tvLeaderboardStatus.setText(leaderboardItems.size() + " Players");

                    for (LeaderboardItem item : leaderboardItems) {
                        String userId = item.getUserId();

                        if (userId == null || userId.isEmpty()) {
                            continue;
                        }

                        db.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    if (currentRequest != requestVersion) {
                                        return;
                                    }

                                    String name = userDoc.getString("fullName");

                                    if (name == null || name.trim().isEmpty()) {
                                        name = userDoc.getString("username");
                                    }

                                    if (name == null || name.trim().isEmpty()) {
                                        name = "Player";
                                    }

                                    item.setPlayerName(name);
                                    int position = leaderboardItems.indexOf(item);

                                    if (position >= 0) {
                                        adapter.notifyItemChanged(position);
                                    }
                                });
                    }

                }).addOnFailureListener(e -> {
                    if (currentRequest != requestVersion) {
                        return;
                    }

                    tvLeaderboardStatus.setText("Failed to load leaderboard: " + e.getMessage());
                });
    }
}