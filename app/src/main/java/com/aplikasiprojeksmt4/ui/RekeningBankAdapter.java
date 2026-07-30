package com.aplikasiprojeksmt4.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.models.RekeningBank;
import java.util.List;

public class RekeningBankAdapter extends RecyclerView.Adapter<RekeningBankAdapter.ViewHolder> {

    private List<RekeningBank> listRekening;
    private OnRekeningClickListener listener;

    public interface OnRekeningClickListener {
        void onDeleteClick(int position);
        void onSetUtamaClick(int position);
    }

    public RekeningBankAdapter(List<RekeningBank> listRekening, OnRekeningClickListener listener) {
        this.listRekening = listRekening;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rekening_bank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RekeningBank rekening = listRekening.get(position);

        holder.tvNamaBank.setText(rekening.getNamaBank());
        holder.tvNomorRekening.setText(rekening.getNomorRekening());
        holder.tvAtasNama.setText(rekening.getAtasNama());

        if (rekening.isUtama()) {
            holder.tvBadgeUtama.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnJadikanUtama.setVisibility(View.GONE);
        } else {
            holder.tvBadgeUtama.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnJadikanUtama.setVisibility(View.VISIBLE);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(position));
        holder.btnJadikanUtama.setOnClickListener(v -> listener.onSetUtamaClick(position));
    }

    @Override
    public int getItemCount() {
        return listRekening.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaBank, tvNomorRekening, tvAtasNama, tvBadgeUtama, btnJadikanUtama;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaBank = itemView.findViewById(R.id.tv_nama_bank);
            tvNomorRekening = itemView.findViewById(R.id.tv_nomor_rekening);
            tvAtasNama = itemView.findViewById(R.id.tv_atas_nama);
            tvBadgeUtama = itemView.findViewById(R.id.tv_badge_utama);
            btnJadikanUtama = itemView.findViewById(R.id.btn_jadikan_utama);
            btnDelete = itemView.findViewById(R.id.btn_delete_rekening);
        }
    }
}