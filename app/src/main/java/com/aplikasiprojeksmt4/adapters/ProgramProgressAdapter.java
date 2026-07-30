package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemProgramProgressBinding;
import com.aplikasiprojeksmt4.models.Program;
import java.util.List;

public class ProgramProgressAdapter extends RecyclerView.Adapter<ProgramProgressAdapter.ViewHolder> {

    private List<Program> programList;

    public ProgramProgressAdapter(List<Program> programList) {
        this.programList = programList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProgramProgressBinding binding = ItemProgramProgressBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Program program = programList.get(position);
        holder.binding.tvProgramName.setText(program.getNama());
        
        long terkumpul = program.getTerkumpul();
        long target = program.getTargetValue();
        
        int progress = 0;
        if (target > 0) {
            progress = (int) ((terkumpul * 100) / target);
        }
        
        holder.binding.tvProgressPercent.setText(progress + "%");
        holder.binding.progressIndicator.setProgress(progress);
        
        // Match colors from screenshot
        if (progress >= 90) {
            holder.binding.tvProgressPercent.setTextColor(android.graphics.Color.parseColor("#FF9800"));
            holder.binding.progressIndicator.setIndicatorColor(android.graphics.Color.parseColor("#FF9800"));
        } else {
            holder.binding.tvProgressPercent.setTextColor(android.graphics.Color.parseColor("#9C27B0"));
            holder.binding.progressIndicator.setIndicatorColor(android.graphics.Color.parseColor("#9C27B0"));
        }
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemProgramProgressBinding binding;
        public ViewHolder(ItemProgramProgressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
