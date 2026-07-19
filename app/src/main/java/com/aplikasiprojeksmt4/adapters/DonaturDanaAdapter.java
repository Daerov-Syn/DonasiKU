package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemDonaturDanaBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DonaturDanaAdapter extends RecyclerView.Adapter<DonaturDanaAdapter.ViewHolder> {

    private List<DonaturDana> donaturList;

    public DonaturDanaAdapter(List<DonaturDana> donaturList) {
        this.donaturList = donaturList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDonaturDanaBinding binding = ItemDonaturDanaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonaturDana donatur = donaturList.get(position);
        
        String nama = donatur.getNamaDonatur() != null ? donatur.getNamaDonatur() : "Anonim";
        holder.binding.tvNamaDonatur.setText(nama);
        holder.binding.tvTanggalDonasi.setText(donatur.getTanggalDonasi());
        
        // Initials
        if (!nama.isEmpty()) {
            String[] parts = nama.split(" ");
            String initial = "";
            if (parts.length >= 2) {
                initial = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
            } else {
                initial = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            }
            holder.binding.tvInitials.setText(initial);
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        holder.binding.tvNominal.setText(formatter.format(donatur.getNominal()));
    }

    @Override
    public int getItemCount() {
        return donaturList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDonaturDanaBinding binding;
        public ViewHolder(ItemDonaturDanaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
