package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemHistoryDonaturBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryUangAdapter extends RecyclerView.Adapter<HistoryUangAdapter.ViewHolder> {

    private List<DonaturDana> historyList;

    public HistoryUangAdapter(List<DonaturDana> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryDonaturBinding binding = ItemHistoryDonaturBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonaturDana donasi = historyList.get(position);
        
        String programName = donasi.getProgramNama();
        if (programName == null || programName.isEmpty()) {
            programName = "Donasi Program";
        }
        holder.binding.tvProgramName.setText(programName);
        
        String detail = donasi.getTanggalDonasi() + " - " + (donasi.getMetodePengiriman() != null ? donasi.getMetodePengiriman() : "Transfer");
        holder.binding.tvTransactionDetail.setText(detail);
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        holder.binding.tvAmount.setText(formatter.format(donasi.getNominal()));
        
        holder.binding.tvStatusHistory.setText("✓ " + (donasi.getStatus() != null ? donasi.getStatus() : "Berhasil"));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryDonaturBinding binding;
        public ViewHolder(ItemHistoryDonaturBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
