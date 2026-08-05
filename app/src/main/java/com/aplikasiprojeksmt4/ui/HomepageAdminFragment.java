package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.HistoryUangAdapter;
import com.aplikasiprojeksmt4.adapters.ProgramProgressAdapter;
import com.aplikasiprojeksmt4.adapters.TopDonaturAdapter;
import com.aplikasiprojeksmt4.databinding.HomepageadminBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomepageAdminFragment extends Fragment {

    private HomepageadminBinding binding;
    private FirebaseFirestore db;

    private TopDonaturAdapter topDonaturAdapter;
    private List<DonaturDana> topDonaturList = new ArrayList<>();

    private ProgramProgressAdapter progressAdapter;
    private List<Program> runningPrograms = new ArrayList<>();

    private HistoryUangAdapter recentTransactionsAdapter;
    private List<DonaturDana> recentTransactions = new ArrayList<>();

    private ListenerRegistration danaListener;
    private ListenerRegistration barangListener;
    private ListenerRegistration programCountListener;
    private ListenerRegistration runningProgramListener;
    private ListenerRegistration recentTransactionListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomepageadminBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();
        loadRealtimeStats();
        loadRunningPrograms();
        loadRecentTransactions();

        binding.btnNotification.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_NotifikasiPageFragment)
        );
        binding.btnProfile.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_ProfileAdminFragment)
        );
        binding.btnVerifikasi.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiAjuanProgramAdminFragment)
        );
        binding.btnDonasiDana.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiDonasiDanaFragment)
        );
        binding.btnDonasiBarang.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiDonasiBarangFragment)
        );
        binding.tvLihatStatistik.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_StatistikFragment)
        );
        setupBottomNavigation(view);
    }

    private void setupRecyclerViews() {
        topDonaturAdapter = new TopDonaturAdapter(topDonaturList);
        binding.rvTopDonatur.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTopDonatur.setAdapter(topDonaturAdapter);

        progressAdapter = new ProgramProgressAdapter(runningPrograms);
        binding.rvProgressProgram.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProgressProgram.setAdapter(progressAdapter);

        recentTransactionsAdapter = new HistoryUangAdapter(recentTransactions);
        binding.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentTransactions.setAdapter(recentTransactionsAdapter);
    }

    private void loadRealtimeStats() {
        // Map to store aggregated data by user name
        Map<String, DonaturDana> userStatsMap = new HashMap<>();

        // 1. Listen to donatur_dana
        danaListener = db.collection("donatur_dana").addSnapshotListener((danaDocs, e1) -> {
            if (binding == null || !isAdded()) return;
            // 2. Listen to donatur_barang
            barangListener = db.collection("donatur_barang").addSnapshotListener((barangDocs, e2) -> {
                if (binding == null || !isAdded()) return;
                
                userStatsMap.clear();
                long totalTransactions = 0;
                long totalBarang = 0;
                long donaturCount = 0;

                // Process Dana
                if (danaDocs != null) {
                    totalTransactions += danaDocs.size();
                    for (QueryDocumentSnapshot doc : danaDocs) {
                        String name = doc.getString("namaDonatur");
                        if (name == null) name = "Anonim";
                        updateMap(userStatsMap, name, 1);
                    }
                }

                // Process Barang
                if (barangDocs != null) {
                    totalTransactions += barangDocs.size();
                    for (QueryDocumentSnapshot doc : barangDocs) {
                        String name = doc.getString("namaDonatur");
                        if (name == null) name = "Anonim";
                        updateMap(userStatsMap, name, 1);
                        
                        String status = doc.getString("status");
                        if ("Diverifikasi".equals(status)) {
                            totalBarang++;
                        }
                    }
                }

                donaturCount = userStatsMap.size();

                // Update UI Header
                binding.tvTotalDana.setText(totalBarang + " Barang");
                binding.tvTransaksiInfo.setText(totalTransactions + " Transaksi - Surabaya");
                
                // Update Cards
                binding.tvCountDonatur.setText(String.valueOf(donaturCount));
                binding.tvCountBarang.setText(String.valueOf(totalBarang));

                // Process Top Donatur List
                List<DonaturDana> sorted = new ArrayList<>(userStatsMap.values());
                Collections.sort(sorted, (d1, d2) -> Integer.compare(d2.getJumlahTransaksi(), d1.getJumlahTransaksi()));

                topDonaturList.clear();
                for (int i = 0; i < Math.min(sorted.size(), 3); i++) {
                    topDonaturList.add(sorted.get(i));
                }
                topDonaturAdapter.notifyDataSetChanged();
            });
        });

        programCountListener = db.collection("programs").whereEqualTo("status", "Aktif").addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;
            if (value != null) {
                binding.tvCountProgram.setText(String.valueOf(value.size()));
            }
        });
    }

    private void updateMap(Map<String, DonaturDana> map, String name, int catCount) {
        if (map.containsKey(name)) {
            DonaturDana existing = map.get(name);
            existing.setJumlahTransaksi(existing.getJumlahTransaksi() + 1);
            existing.setJumlahKategori(existing.getJumlahKategori() + catCount);
        } else {
            DonaturDana d = new DonaturDana();
            d.setNamaDonatur(name);
            d.setJumlahTransaksi(1);
            d.setJumlahKategori(catCount);
            map.put(name, d);
        }
    }

    private void loadRunningPrograms() {
        runningProgramListener = db.collection("programs")
                .whereEqualTo("status", "Aktif")
                .addSnapshotListener((value, error) -> {
                    if (binding == null || !isAdded()) return;
                    if (error != null) return;
                    if (value != null) {
                        runningPrograms.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Program p = doc.toObject(Program.class);
                            if (p != null) {
                                p.setId(doc.getId());
                                runningPrograms.add(p);
                            }
                        }
                        progressAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadRecentTransactions() {
        recentTransactionListener = db.collection("donatur_dana")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    if (binding == null || !isAdded()) return;
                    if (value != null) {
                        recentTransactions.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            DonaturDana d = doc.toObject(DonaturDana.class);
                            if (d != null) {
                                recentTransactions.add(d);
                            }
                        }
                        recentTransactionsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupBottomNavigation(View view) {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_beranda);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) return true;
            if (id == R.id.nav_program_admin) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_PageProgramFragment);
                return true;
            }
            if (id == R.id.nav_statistik) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_StatistikFragment);
                return true;
            }
            if (id == R.id.nav_donatur) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_DonaturAdminFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (danaListener != null) danaListener.remove();
        if (barangListener != null) barangListener.remove();
        if (programCountListener != null) programCountListener.remove();
        if (runningProgramListener != null) runningProgramListener.remove();
        if (recentTransactionListener != null) recentTransactionListener.remove();
        binding = null;
    }
}
