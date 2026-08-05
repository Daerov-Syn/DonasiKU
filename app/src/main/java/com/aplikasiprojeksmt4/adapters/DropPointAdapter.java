package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemDropPointBinding;
import java.util.List;
import java.util.Map;

public class DropPointAdapter extends RecyclerView.Adapter<DropPointAdapter.ViewHolder> {

    private List<Map<String, Object>> dropPointList;
    private OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> item);
    }

    public DropPointAdapter(List<Map<String, Object>> dropPointList, OnItemClickListener listener) {
        this.dropPointList = dropPointList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDropPointBinding binding = ItemDropPointBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = dropPointList.get(position);
        holder.bind(item, position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return dropPointList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDropPointBinding binding;

        public ViewHolder(ItemDropPointBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Map<String, Object> item, boolean isSelected) {
            binding.tvPointName.setText((String) item.get("nama"));
            binding.tvPointAddress.setText((String) item.get("alamat"));
            binding.tvPointType.setText((String) item.get("tipe"));
            binding.tvPointPhoneHours.setText((String) item.get("kontak") + " • " + (String) item.get("jamOperasional"));

            if (isSelected) {
                binding.cardDropPoint.setStrokeColor(binding.getRoot().getContext().getResources().getColor(com.aplikasiprojeksmt4.R.color.primary_purple));
                binding.cardDropPoint.setStrokeWidth(4);
            } else {
                binding.cardDropPoint.setStrokeColor(0xFFEEEEEE);
                binding.cardDropPoint.setStrokeWidth(2);
            }
        }
    }
}
