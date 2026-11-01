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
public class AdminResultAdapter extends RecyclerView.Adapter<AdminResultHolder> {
    private final List<AdminResultItem> items;
    public AdminResultAdapter(List<AdminResultItem> items){this.items=items;}
    @NonNull @Override public AdminResultHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.pq_admin_result_item,parent,false);
        return new AdminResultHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull AdminResultHolder holder,int position){
        AdminResultItem item=items.get(position);
        holder.tvPlayer.setText((position+1)+". "+item.getPlayerName());
        holder.tvScore.setText("Score: "+item.getScore()+" / "+item.getTotalQuestions()+" ("+item.getPercentage()+"%)");
        if(item.getCompletedAt()!=null){
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault());
            holder.tvDate.setText("Completed: "+sdf.format(item.getCompletedAt().toDate()));
        } else holder.tvDate.setText("Completed: Unknown");
    }
    @Override public int getItemCount(){return items.size();}
}
