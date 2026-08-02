package com.aplikasiprojeksmt4.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.databinding.FragmentHistoryBinding;
import com.aplikasiprojeksmt4.models.Riwayat;
import com.aplikasiprojeksmt4.adapters.RiwayatAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private RiwayatAdapter riwayatAdapter;
    private List<Riwayat> allRiwayatList = new ArrayList<>();
    private List<Riwayat> filteredList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentActiveTab = "Semua";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        riwayatAdapter = new RiwayatAdapter(filteredList);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(riwayatAdapter);

        // Langsung panggil data dari donatur_dana
        fetchRiwayatFromDatabase();

        binding.tabSemua.setOnClickListener(v -> {
            setActiveTab(binding.tabSemua);
            currentActiveTab = "Semua";
            filterData(currentActiveTab);
        });

        binding.tabUang.setOnClickListener(v -> {
            setActiveTab(binding.tabUang);
            currentActiveTab = "Uang";
            filterData(currentActiveTab);
        });

        binding.tabBarang.setOnClickListener(v -> {
            setActiveTab(binding.tabBarang);
            currentActiveTab = "Barang";
            filterData(currentActiveTab);
        });

        binding.tabProses.setOnClickListener(v -> {
            setActiveTab(binding.tabProses);
            currentActiveTab = "Proses";
            filterData(currentActiveTab);
        });
    }

    private void fetchRiwayatFromDatabase() {
        // MENGGUNAKAN KOLEKSI ASLI: donatur_dana
        db.collection("donatur_dana")
                // orderBy digunakan jika data Anda punya field 'timestamp'.
                // Jika tidak punya, aplikasi bisa crash. Jadi saya amankan dulu tanpa orderBy.
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Gagal memuat riwayat", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        allRiwayatList.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            // Ambil data sesuai format struktur yang Anda buat di donatur_dana
                            String judul = doc.getString("programNama");
                            String tanggal = doc.getString("tanggalDonasi");
                            String statusRaw = doc.getString("status");

                            // Logika untuk Nominal / Jumlah
                            Long nominalDb = doc.getLong("nominal");
                            String nominalFormat = "-";
                            if (nominalDb != null) {
                                nominalFormat = "Rp " + String.format("%,d", nominalDb).replace(',', '.');
                            }

                            // Konversi status agar seragam (Tersalurkan/Diproses)
                            String statusFinal = "Diproses";
                            if (statusRaw != null) {
                                if (statusRaw.equalsIgnoreCase("Berhasil") || statusRaw.equalsIgnoreCase("Success")) {
                                    statusFinal = "Tersalurkan";
                                } else {
                                    statusFinal = "Diproses";
                                }
                            }

                            // Cegah NullPointerException
                            judul = (judul != null) ? judul : "Tanpa Judul";
                            tanggal = (tanggal != null) ? tanggal : "-";
                            String kategori = "Uang"; // Default karena donatur_dana biasanya transaksi uang

                            allRiwayatList.add(new Riwayat(judul, tanggal, statusFinal, nominalFormat, kategori));
                        }

                        // Segarkan layar dengan data baru
                        filterData(currentActiveTab);
                    }
                });
    }

    private void filterData(String kategori) {
        filteredList.clear();

        if (kategori.equals("Semua")) {
            filteredList.addAll(allRiwayatList);
        } else if (kategori.equals("Proses")) {
            for (Riwayat r : allRiwayatList) {
                if (r.getStatus().equalsIgnoreCase("Diproses")) {
                    filteredList.add(r);
                }
            }
        } else {
            for (Riwayat r : allRiwayatList) {
                if (r.getKategori().equalsIgnoreCase(kategori)) {
                    filteredList.add(r);
                }
            }
        }

        riwayatAdapter.updateData(filteredList);
    }

    private void setActiveTab(MaterialButton activeTab) {
        MaterialButton[] allTabs = {binding.tabSemua, binding.tabUang, binding.tabBarang, binding.tabProses};
        for (MaterialButton tab : allTabs) {
            tab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF")));
            tab.setTextColor(Color.WHITE);
        }
        activeTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        activeTab.setTextColor(Color.parseColor("#333333"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}