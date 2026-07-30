package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ItemImpactBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ImpactAdapter extends RecyclerView.Adapter<ImpactAdapter.ImpactViewHolder> {

    private List<Program> programs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Program program);
    }

    public ImpactAdapter(List<Program> programs) {
        this.programs = programs;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImpactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImpactBinding binding = ItemImpactBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ImpactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImpactViewHolder holder, int position) {
        Program program = programs.get(position);
        holder.binding.tvImpactTitle.setText(program.getNama());
        holder.binding.tvImpactDescription.setText(program.getDeskripsi());
        holder.binding.tvDonaturCount.setText(program.getDonatur_count() + " donatur");
        holder.binding.tvPenerimaCount.setText(program.getPenerima_count() + " jiwa");

        if (program.getCreated_at() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            holder.binding.tvDate.setText("📅 " + sdf.format(program.getCreated_at()));
        }

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(program.getImageUrl())
                    .placeholder(R.drawable.bg_welcomepage)
                    .into(holder.binding.ivImpactImage);
        } else {
            holder.binding.ivImpactImage.setImageResource(R.drawable.bg_welcomepage);
        }

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

    public static class ImpactViewHolder extends RecyclerView.ViewHolder {
        ItemImpactBinding binding;

        public ImpactViewHolder(ItemImpactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
