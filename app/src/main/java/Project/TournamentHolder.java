package Project;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

public class TournamentHolder extends RecyclerView.ViewHolder {

    TextView tvTitle, tvCategory, tvDifficulty, tvType, tvStatus;
    Button btnEdit, btnDelete;

    public TournamentHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.tv_tournamentTitle);
        tvCategory = itemView.findViewById(R.id.tv_tournamentCategory);
        tvDifficulty = itemView.findViewById(R.id.tv_tournamentDifficulty);
        tvType = itemView.findViewById(R.id.tv_tournamentType);
        tvStatus = itemView.findViewById(R.id.tv_tournamentStatus);

        btnEdit = itemView.findViewById(R.id.btn_edit);
        btnDelete = itemView.findViewById(R.id.btn_delete);
    }
}