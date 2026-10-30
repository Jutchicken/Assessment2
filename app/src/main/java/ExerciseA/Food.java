package ExerciseA;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Food extends AppCompatActivity {
    private final List<FoodItem> allFoods = new ArrayList<>();
    private final List<FoodItem> foodList = new ArrayList<>();


    Button btnBack;
    private FoodAdapter adapter;
    private FirebaseFirestore db;

    private RecyclerView rvFoods;
    private View cvAddFood;
    private View tvEmptyFoods;
    private EditText etSearchFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ea_activity_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.excerciseA), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        rvFoods = findViewById(R.id.rv_foods);
        cvAddFood = findViewById(R.id.cv_addFood);
        tvEmptyFoods = findViewById(R.id.tv_emptyFoods);
        etSearchFood = findViewById(R.id.et_searchFood);
        btnBack = findViewById(R.id.btn_back);

        db = FirebaseFirestore.getInstance();


        adapter = new FoodAdapter(foodList, new FoodAdapter.OnFoodActionListener() {
                    @Override
                    public void onEdit(FoodItem food) {
                        showFoodDialog(food);
                    }

                    @Override
                    public void onDelete(FoodItem food) {
                        confirmDelete(food);
                    }
                }
        );

        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        rvFoods.setAdapter(adapter);

        cvAddFood.setOnClickListener(v -> showFoodDialog(null));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                    "Back to Main Page",
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                searchFoods(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadFoods();
    }

    private void loadFoods() {
        db.collection("foods")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                allFoods.clear();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    FoodItem food = document.toObject(FoodItem.class);
                    if (food != null) {
                        food.setId(document.getId());
                        allFoods.add(food);
                    }
                }

                searchFoods(etSearchFood.getText().toString());
            })
            .addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(),
                    "Failed to load foods: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show()
            );
    }

    private void searchFoods(String keyword) {
        foodList.clear();

        String search = keyword.trim().toLowerCase(java.util.Locale.ROOT);

        for (FoodItem food : allFoods) {
            if (food.getName() != null &&
                food.getName().toLowerCase(java.util.Locale.ROOT)
                .contains(search)) {
                    foodList.add(food);
            }
        }

        adapter.notifyDataSetChanged();

        tvEmptyFoods.setVisibility(
                foodList.isEmpty() ? View.VISIBLE : View.GONE
        );
    }
    private void showFoodDialog(FoodItem food) {
        boolean isEdit = food != null;

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        EditText etName = new EditText(getApplicationContext());
        etName.setHint("Food Name");

        EditText etCategory = new EditText(getApplicationContext());
        etCategory.setHint("Category");

        EditText etPrice = new EditText(getApplicationContext());
        etPrice.setHint("Price");
        etPrice.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        EditText etDescription = new EditText(getApplicationContext());
        etDescription.setHint("Description");

        layout.addView(etName);
        layout.addView(etCategory);
        layout.addView(etPrice);
        layout.addView(etDescription);

        if (isEdit) {
            etName.setText(food.getName());
            etCategory.setText(food.getCategory());
            etPrice.setText(String.valueOf(food.getPrice()));
            etDescription.setText(food.getDescription());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Food" : "Add Food")
                .setView(layout)
                .setPositiveButton(isEdit ? "Update" : "Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            String name = etName.getText().toString().trim();
                            String category = etCategory.getText().toString().trim();
                            String priceText = etPrice.getText().toString().trim();
                            String description = etDescription.getText().toString().trim();

                            if (name.isEmpty() || category.isEmpty()
                                    || priceText.isEmpty()
                                    || description.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            double price;

                            try {
                                price = Double.parseDouble(priceText);
                            } catch (NumberFormatException e) {
                                etPrice.setError("Invalid price");
                                return;
                            }

                            if (!Double.isFinite(price) || price < 0) {
                                etPrice.setError("Enter a valid price");
                                return;
                            }

                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(false);

                            if (isEdit) {
                                updateFood(food.getId(), name, category,
                                        price, description, dialog);
                            } else {
                                addFood(name, category, price,
                                        description, dialog);
                            }
                        })
        );

        dialog.show();
    }

    private Map<String, Object> createFoodData(
            String name,
            String category,
            double price,
            String description
    ) {
        Map<String, Object> data = new HashMap<>();

        data.put("name", name);
        data.put("category", category);
        data.put("price", price);
        data.put("description", description);

        return data;
    }

    private void addFood(
            String name,
            String category,
            double price,
            String description,
            AlertDialog dialog
    ) {
        Map<String, Object> foodData = createFoodData(name, category, price, description);

        db.collection("foods")
            .add(foodData)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(getApplicationContext(),
                    "Food added",
                    Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadFoods();
                })
                .addOnFailureListener(e -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                    Toast.makeText(getApplicationContext(),
                        "Failed to add food: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFood(
            String id,
            String name,
            String category,
            double price,
            String description,
            AlertDialog dialog
    ) {
        Map<String, Object> foodData = createFoodData(name, category, price, description);

        db.collection("foods")
            .document(id)
            .update(foodData)
            .addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(),
                    "Food updated",
                    Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadFoods();
            })
            .addOnFailureListener(e -> {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                Toast.makeText(getApplicationContext(),
                    "Failed to update food: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void confirmDelete(FoodItem food) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Food")
            .setMessage("Delete " + food.getName() + "?")
            .setPositiveButton("Delete", (dialog, which) ->
                deleteFood(food.getId()))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteFood(String id) {
        db.collection("foods")
            .document(id)
            .delete()
            .addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(),
                    "Food deleted",
                    Toast.LENGTH_SHORT).show();
                loadFoods();
            })
            .addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(),
                    "Failed to delete food: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show()
            );
    }
}