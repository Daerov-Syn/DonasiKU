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

import com.aplikasiprojeksmt4.databinding.PageDetailVerifikasiBarangBinding;

public class DetailVerifikasiBarangFragment extends Fragment {

    private PageDetailVerifikasiBarangBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menyambungkan file Java ini dengan desain XML page_detail_verifikasi_barang
        binding = PageDetailVerifikasiBarangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Logika Tombol Kembali (Pojok Kiri Atas)
        binding.btnBackDetail.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        // 2. Logika Tombol Setujui
        binding.btnSetujuDetail.setOnClickListener(v -> {
            // Untuk sementara kita berikan pesan Toast (Pesan Pop-up di bawah)
            // Nanti di sini kita bisa tambahkan logika update ke Firebase
            // dan pindah ke halaman "Sukses Disetujui"
            Toast.makeText(getContext(), "Donasi Barang Berhasil Disetujui!", Toast.LENGTH_SHORT).show();
        });

        // 3. Logika Tombol Tolak
        // binding.btnTolakDetail.setOnClickListener(...);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Menghindari memory leak
    }
}