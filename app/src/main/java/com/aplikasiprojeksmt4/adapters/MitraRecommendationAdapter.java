package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemMitraRecommendationBinding;
import com.aplikasiprojeksmt4.models.Program;
import java.util.List;

public class MitraRecommendationAdapter extends RecyclerView.Adapter<MitraRecommendationAdapter.ViewHolder> {

    private List<Program> programList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Program program);
    }

    public MitraRecommendationAdapter(List<Program> programList, OnItemClickListener listener) {
        this.programList = programList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMitraRecommendationBinding binding = ItemMitraRecommendationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Program program = programList.get(position);
        holder.bind(program, position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onItemClick(program);
        });
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMitraRecommendationBinding binding;

        public ViewHolder(ItemMitraRecommendationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Program program, boolean isSelected) {
            binding.tvMitraName.setText(program.getNama());
            binding.tvKategori.setText("Kategori (" + program.getTipe() + ")");
            
            // Set Icon based on category
            if ("Barang".equalsIgnoreCase(program.getTipe())) {
                binding.ivKategoriIcon.setImageResource(com.aplikasiprojeksmt4.R.drawable.dus);
            } else {
                binding.ivKategoriIcon.setImageResource(com.aplikasiprojeksmt4.R.drawable.uang);
            }

            // Calculate dummy match percentage based on remaining target (higher remaining = higher priority)
            long target = program.getTargetValue();
            long terkumpul = program.getTerkumpul();
            int percent = 0;
            if (target > 0) {
                float needRatio = (float) (target - terkumpul) / target;
                percent = (int) (needRatio * 100);
            } else {
                percent = 50;
            }
            
            binding.tvMatchPercent.setText(percent + "%");
            
            // Set default distance to 0 km if no real location data
            binding.tvDistanceStatus.setText("0 km • Sedang membutuhkan");

            binding.tvKapasitas.setText("Kapasitas tersedia: " + (target - terkumpul) + " slot lagi");

            if (isSelected) {
                binding.cardMitra.setStrokeColor(binding.getRoot().getContext().getResources().getColor(com.aplikasiprojeksmt4.R.color.primary_purple));
                binding.cardMitra.setStrokeWidth(4);
            } else {
                binding.cardMitra.setStrokeColor(0xFFEEEEEE);
                binding.cardMitra.setStrokeWidth(2);
            }
        }
    }
}
