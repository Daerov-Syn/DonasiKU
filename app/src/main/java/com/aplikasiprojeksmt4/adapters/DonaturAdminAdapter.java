package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemDonaturAdminBinding;
import com.aplikasiprojeksmt4.models.User;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DonaturAdminAdapter extends RecyclerView.Adapter<DonaturAdminAdapter.ViewHolder> {

    private List<User> userList;
    private Map<String, Long> donationTotals;
    private Map<String, Integer> donationCounts;

    public DonaturAdminAdapter(List<User> userList, Map<String, Long> donationTotals, Map<String, Integer> donationCounts) {
        this.userList = userList;
        this.donationTotals = donationTotals;
        this.donationCounts = donationCounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDonaturAdminBinding binding = ItemDonaturAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.tvName.setText(user.getNama());
        holder.binding.tvEmail.setText(user.getEmail());
        holder.binding.tvAddress.setText(user.getAlamat() != null ? user.getAlamat() : "Alamat belum diatur");
        
        if (user.getNama() != null && !user.getNama().isEmpty()) {
            holder.binding.tvInitial.setText(user.getNama().substring(0, 1).toUpperCase());
        }

        long total = donationTotals.containsKey(user.getId()) ? donationTotals.get(user.getId()) : 0;
        int count = donationCounts.containsKey(user.getId()) ? donationCounts.get(user.getId()) : 0;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        holder.binding.tvTotalDonation.setText(formatter.format(total));
        holder.binding.tvTransactionInfo.setText(count + "x transaksi");

        if (count > 0) {
            holder.binding.tvStatus.setText("Aktif");
            holder.binding.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E8F5E9")));
            holder.binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else {
            holder.binding.tvStatus.setText("Tidak Aktif");
            holder.binding.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFEBEE")));
            holder.binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<User> newList, Map<String, Long> newTotals, Map<String, Integer> newCounts) {
        this.userList = newList;
        this.donationTotals = newTotals;
        this.donationCounts = newCounts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDonaturAdminBinding binding;
        public ViewHolder(ItemDonaturAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
