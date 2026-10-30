package ExerciseA;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

public class FoodHolder extends RecyclerView.ViewHolder {

    TextView tvFoodName, tvFoodCategory, tvFoodPrice, tvFoodDescription;
    Button btnEdit, btnDelete;

    public FoodHolder(@NonNull View itemView) {
        super(itemView);
        tvFoodName = itemView.findViewById(R.id.tv_foodName);
        tvFoodCategory = itemView.findViewById(R.id.tv_foodCategory);
        tvFoodPrice = itemView.findViewById(R.id.tv_foodPrice);
        tvFoodDescription = itemView.findViewById(R.id.tv_foodDescription);

        btnEdit = itemView.findViewById(R.id.btn_edit);
        btnDelete = itemView.findViewById(R.id.btn_delete);
    }
}
