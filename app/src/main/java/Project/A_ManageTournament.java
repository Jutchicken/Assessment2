package Project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class A_ManageTournament extends AppCompatActivity {

    Button btnBack;
    RecyclerView rvTournaments;
    TextView tvEmptyTournaments;
    TournamentAdapter adapter;
    List<TournamentItem> tournamentList = new ArrayList<>();
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_manage_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        rvTournaments = findViewById(R.id.rv_tournaments);
        tvEmptyTournaments = findViewById(R.id.tv_emptyTournaments);
        database = FirebaseFirestore.getInstance();

        adapter = new TournamentAdapter(tournamentList, new TournamentAdapter.OnTournamentActionListener() {
            @Override
            public void onEdit(TournamentItem tournament) {
                editTournament(tournament);
            }

            @Override
            public void onDelete(TournamentItem tournament) {
                deleteTournament(tournament);
            }
        });
        rvTournaments.setLayoutManager(new LinearLayoutManager(this));
        rvTournaments.setAdapter(adapter);
        loadTournament();
    }

    private void editTournament(TournamentItem tournament) {
        EditText etTitle = new EditText(this);
        etTitle.setText(tournament.getTitle());
        etTitle.setHint("Tournament Title");

        String[] statuses = {
                "Open",
                "Closed"
        };

        int selectedStatus = "Closed".equalsIgnoreCase(tournament.getStatus()) ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Edit Tournament")
                .setView(etTitle)
                .setSingleChoiceItems(statuses, selectedStatus,
                        (dialog, which) -> {
                            dialog.dismiss();
                            showEditDialog(tournament, etTitle.getText().toString(), statuses[which]);
                        }
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(TournamentItem tournament, String title, String status) {
        EditText etTitle = new EditText(this);
        etTitle.setText(title);
        etTitle.setHint("Tournament Title");

        new AlertDialog.Builder(this)
                .setTitle("Update Tournament")
                .setView(etTitle)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newTitle = etTitle.getText().toString().trim();

                    if (newTitle.isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "Please enter Tournament Title",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    database.collection("tournaments")
                            .document(tournament.getId())
                            .update("title", newTitle, "status", status)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(),
                                        "Tournament Updated",
                                        Toast.LENGTH_SHORT).show();
                                loadTournament();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                }).setNegativeButton("Cancel", null).show();
    }

    private void deleteTournament(TournamentItem tournament) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tournament")
                .setMessage("Are you sure you want to delete " + tournament.getTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    database.collection("tournaments")
                            .document(tournament.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(),
                                        "Tournament Deleted",
                                        Toast.LENGTH_SHORT).show();
                                loadTournament();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                }).setNegativeButton("Cancel", null).show();
    }

    private void loadTournament() {
        database.collection("tournaments")
                .get()
                .addOnSuccessListener(snapshot -> {
                    tournamentList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        TournamentItem tournament = document.toObject(TournamentItem.class);

                        if(tournament != null) {
                            tournament.setId(document.getId());
                            tournamentList.add(tournament);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),
                            "Error: "+ e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}