package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemTopDonaturBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import java.util.List;

public class TopDonaturAdapter extends RecyclerView.Adapter<TopDonaturAdapter.ViewHolder> {

    private List<DonaturDana> topDonaturList;

    public TopDonaturAdapter(List<DonaturDana> topDonaturList) {
        this.topDonaturList = topDonaturList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTopDonaturBinding binding = ItemTopDonaturBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonaturDana donatur = topDonaturList.get(position);

        holder.binding.tvNamaDonatur.setText(donatur.getNamaDonatur());

        holder.binding.tvTransaksiCount.setText(donatur.getJumlahTransaksi() + "x transaksi");

        holder.binding.tvTotalDonasi.setText(donatur.getJumlahKategori() + " Kategori Barang");

        if (position == 0) holder.binding.ivMedal.setColorFilter(android.graphics.Color.parseColor("#FFD700")); //
        else if (position == 1) holder.binding.ivMedal.setColorFilter(android.graphics.Color.parseColor("#C0C0C0")); //
        else if (position == 2) holder.binding.ivMedal.setColorFilter(android.graphics.Color.parseColor("#CD7F32")); //
        else holder.binding.ivMedal.setVisibility(View.INVISIBLE);

        // 5. Hilangkan garis pemisah di item paling terakhir
        if (position == topDonaturList.size() - 1) {
            holder.binding.divider.setVisibility(View.GONE);
        } else {
            holder.binding.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(topDonaturList.size(), 3);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTopDonaturBinding binding;
        public ViewHolder(ItemTopDonaturBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}