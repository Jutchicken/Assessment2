package Project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardHolder>{

    private final List<LeaderboardItem> items;
    public LeaderboardAdapter(List<LeaderboardItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public LeaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pq_leaderboard_item, parent, false);
        return new LeaderboardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardHolder holder, int position) {
        LeaderboardItem item = items.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvPlayerName.setText(item.getPlayerName());
        holder.tvLeaderboardScore.setText(item.getScore() + " / " + item.getTotalQuestions());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
