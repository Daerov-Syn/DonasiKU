package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ItemProgramAdminBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {

    private List<Program> programs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Program program);
    }

    public ProgramAdapter(List<Program> programs) {
        this.programs = programs;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProgramAdminBinding binding = ItemProgramAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProgramViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        Program program = programs.get(position);
        holder.binding.tvNamaProgram.setText(program.getNama());
        holder.binding.tvWilayah.setText(program.getWilayah());
        
        long target = program.getTargetValue();
        long terkumpul = program.getTerkumpul();
        int progress = 0;
        if (target > 0) {
            progress = (int) ((terkumpul * 100) / target);
        }
        
        holder.binding.progressIndicator.setProgress(progress);
        
        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            holder.binding.tvTerkumpul.setText("Rp " + String.format("%,d", terkumpul).replace(',', '.') + " terkumpul");
        } else {
            String unit = program.getTargetUnit();
            holder.binding.tvTerkumpul.setText(terkumpul + " " + unit + " terkumpul");
        }
        
        holder.binding.tvPersentaseTarget.setText(progress + "% dari target");

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(program.getImageUrl())
                    .placeholder(R.drawable.group_2)
                    .into(holder.binding.ivProgramImage);
        } else {
            holder.binding.ivProgramImage.setImageResource(R.drawable.group_2);
        }

        // Tombol Lihat Detail sesuai permintaan user
        holder.binding.btnLihatDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(program);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(program);
            }
        });
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public void setPrograms(List<Program> newPrograms) {
        this.programs = newPrograms;
        notifyDataSetChanged();
    }

    public static class ProgramViewHolder extends RecyclerView.ViewHolder {
        ItemProgramAdminBinding binding;

        public ProgramViewHolder(ItemProgramAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
