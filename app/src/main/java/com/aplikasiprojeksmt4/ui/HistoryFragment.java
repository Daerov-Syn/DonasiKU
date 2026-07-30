package com.aplikasiprojeksmt4.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentHistoryBinding;
import com.google.android.material.button.MaterialButton;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Tombol Kembali
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 2. Setup Awal RecyclerView (Siapkan tempat untuk list riwayat)
        setupRecyclerView();

        // 3. Logika Klik Tab / Pagination
        binding.tabSemua.setOnClickListener(v -> {
            setActiveTab(binding.tabSemua);
            // TODO: Filter dan tampilkan SEMUA riwayat di Adapter
        });

        binding.tabUang.setOnClickListener(v -> {
            setActiveTab(binding.tabUang);
            // TODO: Filter dan tampilkan riwayat kategori UANG saja
        });

        binding.tabBarang.setOnClickListener(v -> {
            setActiveTab(binding.tabBarang);
            // TODO: Filter dan tampilkan riwayat kategori BARANG saja
        });

        binding.tabProses.setOnClickListener(v -> {
            setActiveTab(binding.tabProses);
            // TODO: Filter dan tampilkan riwayat berstatus DIPROSES saja
        });
    }

    private void setupRecyclerView() {
        // Mengatur susunan list menjadi ke bawah (vertikal)
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TODO: Nanti Anda pasang Adapter Anda di sini. Contoh:
        // riwayatAdapter = new RiwayatAdapter(riwayatList);
        // binding.rvHistory.setAdapter(riwayatAdapter);

        // Catatan:
        // Aksi klik untuk menuju 'DetailDonasiFragment' nanti ditaruh di dalam Adapter (RiwayatAdapter),
        // BUKAN di HistoryFragment lagi.
    }

    // Fungsi untuk mengubah warna Tab yang sedang aktif
    private void setActiveTab(MaterialButton activeTab) {
        // A. Reset semua tab menjadi transparan (warna awal)
        MaterialButton[] allTabs = {binding.tabSemua, binding.tabUang, binding.tabBarang, binding.tabProses};
        for (MaterialButton tab : allTabs) {
            tab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF"))); // Putih Transparan
            tab.setTextColor(Color.WHITE); // Teks Putih
        }

        // B. Set tab yang diklik menjadi aktif (Putih Solid, Teks Hitam)
        activeTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        activeTab.setTextColor(Color.parseColor("#333333"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}