package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.PageDaftarDonaturMitraBinding;
// Asumsi Anda memiliki Model bernama DonaturDana
import com.aplikasiprojeksmt4.models.DonaturDana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaftarDonaturMitraFragment extends Fragment {

    private PageDaftarDonaturMitraBinding binding;
    private List<DonaturDana> listDonatur = new ArrayList<>();
    // private AdapterDonatur adapter; // Aktifkan jika adapter sudah ada

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageDaftarDonaturMitraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Tombol Kembali
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 2. Setup RecyclerView
        binding.rvSemuaDonatur.setLayoutManager(new LinearLayoutManager(requireContext()));
        // adapter = new AdapterDonatur(listDonatur);
        // binding.rvSemuaDonatur.setAdapter(adapter);

        // 3. Tarik Data (Bisa diganti tarikan dari Firebase)
        loadMockData();

        // 4. Logika Tombol Urutkan Terbaru
        binding.btnSortTerbaru.setOnClickListener(v -> {
            setSortButtonActive(true); // Ubah warna tombol
            sortDataByTerbaru();
        });

        // 5. Logika Tombol Urutkan Terbesar
        binding.btnSortTerbesar.setOnClickListener(v -> {
            setSortButtonActive(false); // Ubah warna tombol
            sortDataByTerbesar();
        });
    }

    // --- FUNGSI PENGURUTAN (SORTING) ---
    private void sortDataByTerbaru() {
        // Mengurutkan berdasarkan Waktu/Timestamp (Z-A / Paling baru ke lama)
        Collections.sort(listDonatur, (d1, d2) -> d2.getTimestamp().compareTo(d1.getTimestamp()));
        updateUI();
    }

    private void sortDataByTerbesar() {
        // Mengurutkan berdasarkan Nominal Donasi (Besar ke Kecil)
        Collections.sort(listDonatur, (d1, d2) -> Double.compare(d2.getNominal(), d1.getNominal()));
        updateUI();
    }

    // --- FUNGSI UPDATE TAMPILAN ---
    private void updateUI() {
        // 1. Update Top 3 di Podium (Cek apakah data minimal ada 3)
        if (listDonatur.size() >= 3) {
            DonaturDana top1 = listDonatur.get(0);
            DonaturDana top2 = listDonatur.get(1);
            DonaturDana top3 = listDonatur.get(2);

            binding.tvTop1Nama.setText(top1.getNamaDonatur());
            binding.tvTop1Nominal.setText("Rp " + top1.getNominal());
            // binding.tvTop1Inisial.setText(...);

            // Lakukan hal yang sama untuk top2 dan top3
        }

        // 2. Refresh RecyclerView List Bawah
        // if (adapter != null) adapter.notifyDataSetChanged();
    }

    // --- FUNGSI PEWARNAAN TOMBOL ---
    private void setSortButtonActive(boolean isTerbaru) {
        if (isTerbaru) {
            binding.btnSortTerbaru.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
            binding.btnSortTerbaru.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

            binding.btnSortTerbesar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            binding.btnSortTerbesar.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_purple));
        } else {
            binding.btnSortTerbesar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
            binding.btnSortTerbesar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

            binding.btnSortTerbaru.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            binding.btnSortTerbaru.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_purple));
        }
    }

    private void loadMockData() {
        // Tempat Anda menarik data Firebase.
        // Setelah data masuk ke listDonatur, panggil sortDataByTerbaru() sebagai default awalan.
        sortDataByTerbaru();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}