package Project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

public class ResultHolder  extends RecyclerView.ViewHolder{
    TextView tvResultTitle, tvResultScore, tvResultDate;
    public ResultHolder(@NonNull View itemView) {
        super(itemView);

        tvResultTitle = itemView.findViewById(R.id.tvResultTitle);
        tvResultScore = itemView.findViewById(R.id.tvResultScore);
        tvResultDate = itemView.findViewById(R.id.tvResultDate);
    }
}
