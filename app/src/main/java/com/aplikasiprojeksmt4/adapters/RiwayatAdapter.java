package com.aplikasiprojeksmt4.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.models.Riwayat;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private List<Riwayat> riwayatList;

    public RiwayatAdapter(List<Riwayat> riwayatList) {
        this.riwayatList = riwayatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Mengambil desain kotak kecil yang sudah kita buat sebelumnya
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_donasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Riwayat riwayat = riwayatList.get(position);

        holder.tvTitle.setText(riwayat.getJudul());
        holder.tvDate.setText(riwayat.getTanggal());
        holder.tvAmount.setText(riwayat.getNominal());
        holder.tvStatus.setText(riwayat.getStatus());

        // Logika Warna Status (Tersalurkan = Hijau, Diproses = Oranye)
        if (riwayat.getStatus().equalsIgnoreCase("Tersalurkan")) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF3E0")));
        }

        // Logika Ikon (Uang vs Barang)
        if (riwayat.getKategori().equalsIgnoreCase("Uang")) {
            holder.cvIcon.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.ivIcon.setImageResource(android.R.drawable.ic_menu_send);
            holder.ivIcon.setColorFilter(Color.parseColor("#FF9800"));
        } else {
            holder.cvIcon.setCardBackgroundColor(Color.parseColor("#FCE4EC"));
            holder.ivIcon.setImageResource(android.R.drawable.ic_input_add);
            holder.ivIcon.setColorFilter(Color.parseColor("#E91E63"));
        }
    }

    @Override
    public int getItemCount() {
        return riwayatList.size();
    }

    // Fungsi untuk memperbarui data saat Tab diklik
    public void updateData(List<Riwayat> newList) {
        this.riwayatList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvStatus, tvAmount;
        MaterialCardView cvIcon;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            cvIcon = itemView.findViewById(R.id.cvIcon);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}