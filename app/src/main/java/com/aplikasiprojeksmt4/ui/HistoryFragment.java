package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.RiwayatAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentHistoryBinding;
import com.aplikasiprojeksmt4.models.Riwayat;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Init RecyclerView
        riwayatAdapter = new RiwayatAdapter(filteredList);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(riwayatAdapter);

        // Fetch Data
        fetchRiwayatFromDatabase();

        // Tab Clicks
        binding.tabSemua.setOnClickListener(v -> setActiveTab(binding.tabSemua));
        binding.tabUang.setOnClickListener(v -> setActiveTab(binding.tabUang));
        binding.tabBarang.setOnClickListener(v -> setActiveTab(binding.tabBarang));
        binding.tabProses.setOnClickListener(v -> setActiveTab(binding.tabProses));
    }

    private void fetchRiwayatFromDatabase() {
        String userId = auth.getUid();
        if (userId == null) return;

        // Listener 1: Donasi Uang
        db.collection("donatur_dana")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((danaDocs, e1) -> {
                    // Listener 2: Donasi Barang
                    db.collection("donatur_barang")
                            .whereEqualTo("userId", userId)
                            .addSnapshotListener((barangDocs, e2) -> {
                                if (binding == null || !isAdded()) return;
                                if (e1 != null || e2 != null) {
                                    Log.e("History", "Error fetching", e1 != null ? e1 : e2);
                                    return;
                                }

                                allRiwayatList.clear();
                                long totalDana = 0;
                                long totalBarang = 0;
                                long totalTersalur = 0;

                                // Process Uang
                                if (danaDocs != null) {
                                    for (QueryDocumentSnapshot doc : danaDocs) {
                                        String judul = doc.getString("programNama");
                                        String tanggal = doc.getString("tanggalDonasi");
                                        String statusRaw = doc.getString("status");
                                        Long nominal = doc.getLong("nominal");

                                        if (nominal != null) totalDana += nominal;

                                        String statusFinal = "Diproses";
                                        if ("Berhasil".equalsIgnoreCase(statusRaw) || "Success".equalsIgnoreCase(statusRaw)) {
                                            statusFinal = "Tersalurkan";
                                            totalTersalur++;
                                        }

                                        String nominalFormat = nominal != null ? "Rp " + NumberFormat.getInstance(new Locale("id", "ID")).format(nominal) : "-";
                                        allRiwayatList.add(new Riwayat(judul != null ? judul : "Donasi Uang", tanggal != null ? tanggal : "-", statusFinal, nominalFormat, "Uang"));
                                    }
                                }

                                // Process Barang
                                if (barangDocs != null) {
                                    for (QueryDocumentSnapshot doc : barangDocs) {
                                        String judul = doc.getString("programNama");
                                        String tanggal = doc.getString("tanggalDonasi");
                                        String statusRaw = doc.getString("status");
                                        
                                        if (tanggal == null) tanggal = "Baru saja";

                                        if ("Diverifikasi".equals(statusRaw)) {
                                            totalBarang++;
                                            totalTersalur++;
                                        }

                                        String statusFinal = "Diverifikasi".equals(statusRaw) ? "Tersalurkan" : "Diproses";
                                        allRiwayatList.add(new Riwayat(judul != null ? judul : "Donasi Barang", tanggal, statusFinal, "1 Paket", "Barang"));
                                    }
                                }

                                // Update Header Stats
                                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                                nf.setMaximumFractionDigits(0);
                                binding.tvHeaderTotalDana.setText(nf.format(totalDana));
                                binding.tvHeaderTotalPaket.setText(totalBarang + " Paket");
                                binding.tvHeaderTotalTersalur.setText(String.valueOf(totalTersalur));

                                filterData(currentActiveTab);
                            });
                });
    }

    private void filterData(String tab) {
        currentActiveTab = tab;
        filteredList.clear();

        if (tab.equals("Semua")) {
            filteredList.addAll(allRiwayatList);
        } else if (tab.equals("Uang")) {
            for (Riwayat r : allRiwayatList) if (r.getKategori().equals("Uang")) filteredList.add(r);
        } else if (tab.equals("Barang")) {
            for (Riwayat r : allRiwayatList) if (r.getKategori().equals("Barang")) filteredList.add(r);
        } else if (tab.equals("Proses")) {
            for (Riwayat r : allRiwayatList) if (r.getStatus().equals("Diproses")) filteredList.add(r);
        }

        riwayatAdapter.notifyDataSetChanged();
    }

    private void setActiveTab(MaterialButton selectedTab) {
        // Reset all
        binding.tabSemua.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white_translucent));
        binding.tabSemua.setTextColor(android.graphics.Color.WHITE);
        binding.tabUang.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white_translucent));
        binding.tabUang.setTextColor(android.graphics.Color.WHITE);
        binding.tabBarang.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white_translucent));
        binding.tabBarang.setTextColor(android.graphics.Color.WHITE);
        binding.tabProses.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white_translucent));
        binding.tabProses.setTextColor(android.graphics.Color.WHITE);

        // Set active
        selectedTab.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        selectedTab.setTextColor(android.graphics.Color.parseColor("#333333"));

        filterData(selectedTab.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
