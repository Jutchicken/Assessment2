package Project;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class P_ViewTournaments extends AppCompatActivity {

    RecyclerView rvTournaments;
    TextView tvEmptyTournaments;

    P_TournamentAdapter adapter;
    List<TournamentItem> tournamentList = new ArrayList<>();

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_view_tournaments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rvTournaments = findViewById(R.id.rv_tournaments);
        tvEmptyTournaments = findViewById(R.id.tv_emptyTournaments);

        database = FirebaseFirestore.getInstance();

        adapter = new P_TournamentAdapter(tournamentList, new P_TournamentAdapter.OnTournamentActionListener() {
                    @Override
                    public void onPlay(TournamentItem tournament) {
                        playTournament(tournament);
                    }
                }
        );
        rvTournaments.setLayoutManager(new LinearLayoutManager(this));
        rvTournaments.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTournament();
    }

    private void loadTournament() {
        tvEmptyTournaments.setText("Loading tournaments...");
        tvEmptyTournaments.setVisibility(View.VISIBLE);

        database.collection("tournaments")
                .whereEqualTo("status", "Open")
                .get()
                .addOnSuccessListener(snapshot -> {
                    tournamentList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        TournamentItem tournament = document.toObject(TournamentItem.class);

                        if (tournament != null) {
                            tournament.setId(document.getId());
                            tournamentList.add(tournament);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvEmptyTournaments.setText("No available tournaments");
                    tvEmptyTournaments.setVisibility(tournamentList.isEmpty() ? View.VISIBLE : View.GONE);
                }).addOnFailureListener(e -> {
                    tvEmptyTournaments.setText("Failed to load tournaments");
                    tvEmptyTournaments.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void playTournament(TournamentItem tournament) {
        Intent intent = new Intent(getApplicationContext(),
                P_Quiz.class
        );

        intent.putExtra("tournamentId", tournament.getId());
        intent.putExtra("title", tournament.getTitle());
        intent.putExtra("category", tournament.getCategory());
        intent.putExtra("difficulty", tournament.getDifficulty());
        intent.putExtra("type", tournament.getType());
        intent.putExtra("amount", tournament.getAmount());

        startActivity(intent);
    }

    public void btnBack(View view) {
        finish();
    }
}