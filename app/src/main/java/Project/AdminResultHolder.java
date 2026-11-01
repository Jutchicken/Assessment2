package Project;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assessment2.R;
public class AdminResultHolder extends RecyclerView.ViewHolder {
    TextView tvPlayer, tvScore, tvDate;
    public AdminResultHolder(@NonNull View view){
        super(view);
        tvPlayer=view.findViewById(R.id.tvAdminResultPlayer);
        tvScore=view.findViewById(R.id.tvAdminResultScore);
        tvDate=view.findViewById(R.id.tvAdminResultDate);
    }
}
