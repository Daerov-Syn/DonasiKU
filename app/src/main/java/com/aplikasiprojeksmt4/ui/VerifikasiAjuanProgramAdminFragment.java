package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.PageVerifikasiAjuanprogramAdminBinding;

public class VerifikasiAjuanProgramAdminFragment extends Fragment {

    private PageVerifikasiAjuanprogramAdminBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageVerifikasiAjuanprogramAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Tombol Kembali
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 2. Tombol Detail untuk Item 1 (Klinik Gratis)
        binding.btnDetail1.setOnClickListener(v -> {
            // PERHATIAN: Pastikan ID action ini sesuai dengan yang ada di nav_graph.xml Anda!
            Navigation.findNavController(v).navigate(R.id.action_verifikasi_ke_detail);
        });

        // Nanti Anda bisa menambahkan logika tombol Setujui dan Tolak di bawah sini
        // binding.btnSetujui1.setOnClickListener(...);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}