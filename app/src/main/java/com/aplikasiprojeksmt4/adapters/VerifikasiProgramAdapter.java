package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.databinding.ItemVerifikasiProgramBinding;
import com.aplikasiprojeksmt4.models.Program;
import java.util.List;

public class VerifikasiProgramAdapter extends RecyclerView.Adapter<VerifikasiProgramAdapter.ViewHolder> {

    private List<Program> programList;
    private OnActionListener listener;

    public interface OnItemClickListener {
        void onItemClick(Program program);
    }

    public interface OnActionListener {
        void onApprove(Program program);
        void onReject(Program program);
        void onDetail(Program program);
    }

    public VerifikasiProgramAdapter(List<Program> programList, OnActionListener listener) {
        this.programList = programList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVerifikasiProgramBinding binding = ItemVerifikasiProgramBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Program program = programList.get(position);
        holder.bind(program, listener);
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVerifikasiProgramBinding binding;

        public ViewHolder(ItemVerifikasiProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Program program, OnActionListener listener) {
            binding.tvNamaProgram.setText(program.getNama());
            binding.tvOrganisasi.setText(program.getDibuat_oleh_nama() != null ? program.getDibuat_oleh_nama() : "Mitra");
            binding.tvDeskripsi.setText(program.getDeskripsi());
            
            // Set tag based on type or priority if needed
            binding.tvTag.setText(program.getTipe());

            binding.btnSetujui.setOnClickListener(v -> listener.onApprove(program));
            binding.btnTolak.setOnClickListener(v -> listener.onReject(program));
            binding.btnDetail.setOnClickListener(v -> listener.onDetail(program));
        }
    }
}
