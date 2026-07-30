package com.aplikasiprojeksmt4.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemDonaturBinding;
import com.aplikasiprojeksmt4.models.DonaturBarang;
import java.util.List;

public class DonaturAdapter extends RecyclerView.Adapter<DonaturAdapter.DonaturViewHolder> {

    private List<DonaturBarang> donaturList;

    public DonaturAdapter(List<DonaturBarang> donaturList) {
        this.donaturList = donaturList;
    }

    @NonNull
    @Override
    public DonaturViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDonaturBinding binding = ItemDonaturBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DonaturViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DonaturViewHolder holder, int position) {
        DonaturBarang donatur = donaturList.get(position);
        holder.binding.tvNamaDonatur.setText(donatur.getNamaDonatur());
        holder.binding.tvDeskripsiDonasi.setText(donatur.getDeskripsi());
        
        if (donatur.getTimestamp() != null) {
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    donatur.getTimestamp().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS);
            holder.binding.tvWaktuDonasi.setText(timeAgo);
        } else {
            holder.binding.tvWaktuDonasi.setText("Baru saja");
        }
    }

    @Override
    public int getItemCount() {
        return donaturList.size();
    }

    public static class DonaturViewHolder extends RecyclerView.ViewHolder {
        ItemDonaturBinding binding;

        public DonaturViewHolder(ItemDonaturBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
