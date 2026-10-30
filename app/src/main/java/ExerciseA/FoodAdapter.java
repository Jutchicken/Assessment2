package ExerciseA;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment2.R;

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodHolder> {

    public interface OnFoodActionListener {
        void onEdit(FoodItem food);
        void onDelete(FoodItem food);
    }

    private final List<FoodItem> items;
    private final OnFoodActionListener listener;

    public FoodAdapter(List<FoodItem> items, OnFoodActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ea_food_item, parent, false);
        return new FoodHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodHolder holder, int position) {
        final FoodItem food = items.get(position);

        holder.tvFoodName.setText(food.getName());
        holder.tvFoodCategory.setText(food.getCategory());
        holder.tvFoodPrice.setText(
                String.format(Locale.getDefault(), "$%.2f", food.getPrice())
        );

        holder.tvFoodDescription.setText(food.getDescription());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(food);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    private String safe(String s) {
        return s == null ? "" : s;
    }

}
