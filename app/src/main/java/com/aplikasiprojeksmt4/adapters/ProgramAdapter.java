package com.aplikasiprojeksmt4.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ItemProgramAdminBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);

        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            holder.binding.tvTerkumpul.setText(nf.format(terkumpul) + " terkumpul");
            holder.binding.tvTargetLabel.setText("Target: " + nf.format(target));
            holder.binding.tvKurangLabel.setText("Kurang " + nf.format(Math.max(0, target - terkumpul)));
        } else {
            String unit = program.getTargetUnit();
            holder.binding.tvTerkumpul.setText(terkumpul + " " + unit + " terkumpul");
            holder.binding.tvTargetLabel.setText("Target: " + target + " " + unit);
            holder.binding.tvKurangLabel.setText("Kurang " + Math.max(0, target - terkumpul) + " " + unit);
        }
        
        holder.binding.tvPersentaseTarget.setText(progress + "%");

        // New Stats from Figma
        holder.binding.tvDonaturCount.setText(String.valueOf(program.getDonatur_count()));
        holder.binding.tvPenerimaCount.setText(String.valueOf(program.getPenerima_count()));
        
        String daysLeft = calculateDaysLeft(program.getBatas_waktu());
        holder.binding.tvHariLagiStat.setText(daysLeft);
        holder.binding.tvDaysBadge.setText(daysLeft + " hari lagi");
        
        holder.binding.tvCategoryBadge.setText(program.getTipe());

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(program.getImageUrl())
                    .placeholder(R.drawable.group_2)
                    .into(holder.binding.ivProgramImage);
        } else {
            holder.binding.ivProgramImage.setImageResource(R.drawable.group_2);
        }

        holder.binding.btnLihatDetail.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(program);
        });

        holder.binding.btnEditProgram.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_EditProgramFragment, bundle);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(program);
        });
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    private String calculateDaysLeft(String deadline) {
        if (deadline == null || deadline.isEmpty()) return "0";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(deadline);
            if (date == null) return "0";
            long diff = date.getTime() - System.currentTimeMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            return String.valueOf(Math.max(0, days));
        } catch (Exception e) {
            return "0";
        }
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
