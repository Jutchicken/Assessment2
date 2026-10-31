package Project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

import java.util.List;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentHolder> {

    public interface OnTournamentActionListener {
        void onEdit(TournamentItem tournament);
        void onDelete(TournamentItem tournament);
    }

    private final List<TournamentItem> items;
    private final OnTournamentActionListener listener;

    public TournamentAdapter(List<TournamentItem> items, OnTournamentActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TournamentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pq_tournament_item, parent, false);
        return new TournamentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentHolder holder, int position) {
        TournamentItem tournament = items.get(position);

        holder.tvTitle.setText(tournament.getTitle());
        holder.tvCategory.setText("Category: " + getCategoryName(tournament.getCategory()));
        holder.tvDifficulty.setText("Difficulty: " + tournament.getDifficulty());
        holder.tvType.setText("Type: " + tournament.getType());
        holder.tvStatus.setText("Status: " + tournament.getStatus());
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(tournament);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(tournament);
            }
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