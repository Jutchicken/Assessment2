package Project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class P_TournamentAdapter extends RecyclerView.Adapter<P_TournamentHolder> {

    public interface OnTournamentActionListener {
        void onPlay(TournamentItem tournament);
    }

    private final List<TournamentItem> items;
    private final OnTournamentActionListener listener;


    public P_TournamentAdapter(List<TournamentItem> items, OnTournamentActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public P_TournamentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pq_player_tournament_item, parent, false);

        return new P_TournamentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull P_TournamentHolder holder, int position) {
        TournamentItem tournament = items.get(position);
        holder.tvTitle.setText(tournament.getTitle());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.btnPlay.setEnabled(false);
        holder.btnPlay.setText("Checking...");

        if (user == null) {
            holder.btnPlay.setText("Login Required");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("results")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("tournamentId", tournament.getId())
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        holder.btnPlay.setText("Play");
                        holder.btnPlay.setEnabled(true);
                        holder.btnPlay.setOnClickListener(v -> {
                            Intent intent = new Intent(v.getContext(), P_Quiz.class);

                            intent.putExtra("tournamentId", tournament.getId());
                            intent.putExtra("title", tournament.getTitle());
                            intent.putExtra("category", tournament.getCategory());
                            intent.putExtra("difficulty", tournament.getDifficulty());
                            intent.putExtra("type", tournament.getType());
                            intent.putExtra("amount", tournament.getAmount());

                            v.getContext().startActivity(intent);
                        });
                    }
                    else {
                        holder.btnPlay.setText("Completed");
                        holder.btnPlay.setEnabled(false);
                        holder.btnPlay.setOnClickListener(null);
                    }
                })
                .addOnFailureListener(e -> {
                    holder.btnPlay.setText("Unavailable");
                    holder.btnPlay.setEnabled(false);
                });
    }

    private String getCategoryName(int category) {
        switch (category) {
            case 9:
                return "General Knowledge";
            case 10:
                return "Books";
            case 11:
                return "Film";
            case 12:
                return "Music";
            case 15:
                return "Video Games";
            case 17:
                return "Science & Nature";
            case 18:
                return "Computers";
            case 19:
                return "Mathematics";
            case 20:
                return "Mythology";
            case 21:
                return "Sports";
            case 22:
                return "Geography";
            case 23:
                return "History";
            default:
                return "Unknown";
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
