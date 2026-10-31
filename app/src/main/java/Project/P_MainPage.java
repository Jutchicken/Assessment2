package Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assessment2.R;
import com.google.firebase.auth.FirebaseAuth;

public class P_MainPage extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_player_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void btnViewTournaments(View view) {
        Toast.makeText(getApplicationContext(),
                "View Tournaments",
                Toast.LENGTH_SHORT).show();
    }

    public void btnMyResults(View view) {
        Toast.makeText(getApplicationContext(),
                "My Results",
                Toast.LENGTH_SHORT).show();
    }

    public void btnLeaderboard(View view) {
        Toast.makeText(getApplicationContext(),
                "Leaderboard",
                Toast.LENGTH_SHORT).show();
    }

    public void btnLogout(View view) {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(),
            Login.class
        );
        startActivity(intent);
        finish();
    }
}