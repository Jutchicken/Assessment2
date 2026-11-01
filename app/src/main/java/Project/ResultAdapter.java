package Project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ResultAdapter extends RecyclerView.Adapter<ResultHolder>{

    private final List<ResultItem> items;

    public ResultAdapter(List<ResultItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pq_result_item, parent, false);

        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        ResultItem item = items.get(position);
        holder.tvResultTitle.setText(item.getTournamentTitle());
        holder.tvResultScore.setText(
                "Score: " + item.getScore()
                        + " / " + item.getTotalQuestions()
                        + " (" + item.getPercentage() + "%)"
        );

        if (item.getCompletedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvResultDate.setText(sdf.format(item.getCompletedAt().toDate()));
        }
        else {
            holder.tvResultDate.setText("Date unavailable");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
