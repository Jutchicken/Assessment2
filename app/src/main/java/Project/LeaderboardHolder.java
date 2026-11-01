package Project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

public class LeaderboardHolder extends RecyclerView.ViewHolder {
    TextView tvRank, tvPlayerName, tvLeaderboardScore;
    public LeaderboardHolder(@NonNull View itemView) {
        super(itemView);

        tvRank = itemView.findViewById(R.id.tvRank);
        tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
        tvLeaderboardScore = itemView.findViewById(R.id.tvLeaderboardScore);
    }
}
