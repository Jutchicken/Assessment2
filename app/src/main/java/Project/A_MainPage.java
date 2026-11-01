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

public class A_MainPage extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void btnCreateTournament(View view) {
        Intent intent = new Intent(getApplicationContext(),
                A_CreateTournament.class);
        startActivity(intent);
    }

    public void btnManageTournament(View view) {
        Intent intent = new Intent(getApplicationContext(),
                A_ManageTournament.class);
        startActivity(intent);
    }

    public void btnViewResults(View view) {
        Intent intent = new Intent(getApplicationContext(),
                A_ViewResults.class);
        startActivity(intent);
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