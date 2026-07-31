package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.databinding.FragmentFaqBinding;

public class FaqFragment extends Fragment {

    private FragmentFaqBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menggunakan ViewBinding agar tidak perlu findViewById
        binding = FragmentFaqBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Logika Tombol Kembali (Navigasi mundur ke Profil)
        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        // 2. Simulasi Klik pada Item FAQ
        // Nanti bisa Anda arahkan untuk membuka halaman jawaban detail
        binding.faqUang1.setOnClickListener(v -> showToast("Cara berdonasi uang..."));
        binding.faqUang2.setOnClickListener(v -> showToast("Tentang transparansi donasi..."));
        binding.faqUang3.setOnClickListener(v -> showToast("Info donasi anonim..."));

        binding.faqBarang1.setOnClickListener(v -> showToast("Jenis barang yang diterima..."));
        binding.faqBarang2.setOnClickListener(v -> showToast("Lokasi drop point..."));
        binding.faqBarang3.setOnClickListener(v -> showToast("Panduan foto barang..."));

        binding.faqAkun1.setOnClickListener(v -> showToast("Cara unduh sertifikat..."));
        binding.faqAkun2.setOnClickListener(v -> showToast("Bantuan reset kata sandi..."));
    }

    // Fungsi singkat untuk memunculkan pesan pop-up
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Mencegah memory leak
    }
}