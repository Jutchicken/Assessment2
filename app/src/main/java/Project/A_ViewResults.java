package Project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assessment2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class A_ViewResults extends AppCompatActivity {
    private Spinner spTournament;
    private TextView tvParticipants,tvAverage,tvStatus;
    private FirebaseFirestore db;
    private final List<TournamentItem> tournaments=new ArrayList<>();
    private final List<AdminResultItem> items=new ArrayList<>();
    private AdminResultAdapter adapter;
    private int requestVersion=0;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pq_activity_admin_results);
        spTournament=findViewById(R.id.spAdminTournament);
        tvParticipants=findViewById(R.id.tvAdminParticipants);
        tvAverage=findViewById(R.id.tvAdminAverage);
        tvStatus=findViewById(R.id.tvAdminResultsStatus);
        RecyclerView rv=findViewById(R.id.rvAdminResults);
        db=FirebaseFirestore.getInstance();
        adapter=new AdminResultAdapter(items);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        findViewById(R.id.btnBackAdminResults).setOnClickListener(v->finish());
        loadTournaments();
    }
    private void loadTournaments(){
        tvStatus.setText("Loading tournaments...");
        db.collection("tournaments").get().addOnSuccessListener(snapshot->{
            tournaments.clear();
            List<String> names=new ArrayList<>();
            for(DocumentSnapshot doc:snapshot.getDocuments()){
                TournamentItem t=doc.toObject(TournamentItem.class);
                if(t==null)continue;
                t.setId(doc.getId());
                tournaments.add(t);
                names.add(t.getTitle()==null?"Untitled":t.getTitle());
            }
            if(tournaments.isEmpty()){
                tvStatus.setText("No tournaments available");return;
            }
            ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,names);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spTournament.setAdapter(spinnerAdapter);
            spTournament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                    loadResults(tournaments.get(position).getId());
                }
                @Override public void onNothingSelected(AdapterView<?> parent){}
            });
        }).addOnFailureListener(e->tvStatus.setText("Failed to load tournaments: "+e.getMessage()));
    }
    private void loadResults(String tournamentId){
        final int version=++requestVersion;
        items.clear();adapter.notifyDataSetChanged();
        tvParticipants.setText("Participants: 0");
        tvAverage.setText("Average Score: 0%");
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("Loading results...");
        db.collection("results").whereEqualTo("tournamentId",tournamentId).get()
            .addOnSuccessListener(snapshot->{
                if(version!=requestVersion)return;
                if(snapshot.isEmpty()){tvStatus.setText("No results yet");return;}
                double totalPercentage=0;
                for(DocumentSnapshot doc:snapshot.getDocuments()){
                    Long s=doc.getLong("score"),t=doc.getLong("totalQuestions"),p=doc.getLong("percentage");
                    int score=s==null?0:s.intValue();
                    int total=t==null?0:t.intValue();
                    int percentage=p==null?(total>0?score*100/total:0):p.intValue();
                    items.add(new AdminResultItem(doc.getString("userId"),"Player",score,total,percentage,doc.getTimestamp("completedAt")));
                    totalPercentage+=percentage;
                }
                items.sort((a,b)->{
                    int c=Integer.compare(b.getScore(),a.getScore());
                    if(c!=0)return c;
                    Timestamp aTime=a.getCompletedAt(),bTime=b.getCompletedAt();
                    if(aTime==null&&bTime==null)return 0;
                    if(aTime==null)return 1;
                    if(bTime==null)return -1;
                    return aTime.compareTo(bTime);
                });
                adapter.notifyDataSetChanged();
                tvParticipants.setText("Participants: "+items.size());
                tvAverage.setText(String.format(Locale.getDefault(),"Average Score: %.1f%%",totalPercentage/items.size()));
                tvStatus.setVisibility(View.GONE);
                for(AdminResultItem item:items){
                    String uid=item.getUserId();
                    if(uid==null||uid.isEmpty())continue;
                    db.collection("users").document(uid).get().addOnSuccessListener(userDoc->{
                        if(version!=requestVersion)return;
                        String name=userDoc.getString("fullName");
                        if(name==null||name.trim().isEmpty())name=userDoc.getString("username");
                        if(name==null||name.trim().isEmpty())name="Player";
                        item.setPlayerName(name);
                        int pos=items.indexOf(item);
                        if(pos>=0)adapter.notifyItemChanged(pos);
                    });
                }
            }).addOnFailureListener(e->{
                if(version==requestVersion)tvStatus.setText("Failed to load results: "+e.getMessage());
            });
    }
}
