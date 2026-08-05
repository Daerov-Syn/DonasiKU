package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemWithdrawalBinding;
import com.aplikasiprojeksmt4.models.Withdrawal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WithdrawalAdapter extends RecyclerView.Adapter<WithdrawalAdapter.ViewHolder> {

    private List<Withdrawal> withdrawalList;

    public WithdrawalAdapter(List<Withdrawal> withdrawalList) {
        this.withdrawalList = withdrawalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWithdrawalBinding binding = ItemWithdrawalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Withdrawal w = withdrawalList.get(position);
        holder.bind(w);
    }

    @Override
    public int getItemCount() {
        return withdrawalList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWithdrawalBinding binding;

        public ViewHolder(ItemWithdrawalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Withdrawal w) {
            binding.tvWithdrawDate.setText(w.getTanggal());
            binding.tvWithdrawMethod.setText("via " + w.getMetode());
            binding.tvWithdrawStatus.setText(w.getStatus());

            // Format amount as -Rp 15.0jt etc to match design
            double amountInJt = (double) w.getNominal() / 1_000_000.0;
            String amountStr;
            if (amountInJt >= 1.0) {
                amountStr = String.format(Locale.getDefault(), "-Rp %.1fjt", amountInJt);
            } else {
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                nf.setMaximumFractionDigits(0);
                amountStr = "-" + nf.format(w.getNominal());
            }
            binding.tvWithdrawAmount.setText(amountStr);
        }
    }
}
