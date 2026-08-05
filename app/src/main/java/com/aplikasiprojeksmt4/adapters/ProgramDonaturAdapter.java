package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemDonaturProgramBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgramDonaturAdapter extends RecyclerView.Adapter<ProgramDonaturAdapter.ViewHolder> {

    private List<DonaturDana> donaturList;

    public ProgramDonaturAdapter(List<DonaturDana> donaturList) {
        this.donaturList = donaturList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDonaturProgramBinding binding = ItemDonaturProgramBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonaturDana d = donaturList.get(position);
        holder.bind(d);
    }

    @Override
    public int getItemCount() {
        return donaturList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDonaturProgramBinding binding;

        public ViewHolder(ItemDonaturProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DonaturDana d) {
            binding.tvDonaturName.setText(d.getNamaDonatur());
            
            // Initial
            if (d.getNamaDonatur() != null && !d.getNamaDonatur().isEmpty()) {
                String[] parts = d.getNamaDonatur().split(" ");
                String initial = "";
                if (parts.length > 0) initial += parts[0].charAt(0);
                if (parts.length > 1) initial += parts[1].charAt(0);
                binding.tvDonaturInitial.setText(initial.toUpperCase());
            }

            // Time
            binding.tvDonasiTime.setText(getTimeAgo(d.getTimestamp()));

            // Amount
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            nf.setMaximumFractionDigits(0);
            binding.tvDonasiAmount.setText(nf.format(d.getNominal()));

            // Message
            if (d.getPesan() != null && !d.getPesan().isEmpty()) {
                binding.cardMessage.setVisibility(View.VISIBLE);
                binding.tvDonaturMessage.setText("“" + d.getPesan() + "”");
            } else {
                binding.cardMessage.setVisibility(View.GONE);
            }
        }

        private String getTimeAgo(Date date) {
            if (date == null) return "Baru saja";
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) return days + " hari lalu";
            if (hours > 0) return hours + " jam lalu";
            if (minutes > 0) return minutes + " menit lalu";
            return "Baru saja";
        }
    }
}
