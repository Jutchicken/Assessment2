package Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assessment2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText etFullName, etEmail, etPassword, etConfirmPassword;
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    Spinner spRole;
    String[] roles = {
            "Player",
            "Admin"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pq_activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFullName = findViewById(R.id.et_fullName);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirmPassword);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        spRole = findViewById(R.id.sp_role);

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                roles
        );

        roleAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spRole.setAdapter(roleAdapter);
    }

    public void btnRegister(View view) {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(),
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                if (firebaseUser == null) {
                    Toast.makeText(getApplicationContext(),
                            "Unable to get user information",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String uid = firebaseUser.getUid();
                Map<String, Object> user = new HashMap<>();

                user.put("fullName", fullName);
                user.put("email", email);
                String role = spRole.getSelectedItem().toString();
                user.put("role", role);

                database.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(),
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();

                            Intent intent = new Intent(getApplicationContext(),
                                    Login.class
                            );

                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(),
                                    "Database Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        });
            } else {
                Toast.makeText(getApplicationContext(),
                        "Registration Failed: "
                                + (task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error"),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void btnLogin(View view) {
        Intent intent = new Intent(getApplicationContext(),
            Login.class
        );
        startActivity(intent);
        finish();
    }
}