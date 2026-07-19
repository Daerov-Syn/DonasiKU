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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        loadDashboardData();
        loadTopDonaturs();
        loadRunningPrograms();
        loadRecentTransactions();

        // Navigation
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

    private void loadDashboardData() {
        db.collection("donatur_dana").addSnapshotListener((value, error) -> {
            if (value != null) {
                long total = 0;
                for (QueryDocumentSnapshot doc : value) {
                    DonaturDana d = doc.toObject(DonaturDana.class);
                    total += d.getNominal();
                }
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatter.setMaximumFractionDigits(0);
                binding.tvTotalDana.setText(formatter.format(total));
                binding.tvTransaksiInfo.setText(value.size() + " Transaksi - Surabaya");
                binding.tvCountDonatur.setText(String.valueOf(value.size()));
            }
        });

        db.collection("programs").whereEqualTo("status", "Aktif").addSnapshotListener((value, error) -> {
            if (value != null) {
                binding.tvCountProgram.setText(String.valueOf(value.size()));
            }
        });
    }

    private void loadTopDonaturs() {
        db.collection("donatur_dana").addSnapshotListener((value, error) -> {
            if (value != null) {
                Map<String, DonaturDana> aggregated = new HashMap<>();
                for (QueryDocumentSnapshot doc : value) {
                    DonaturDana d = doc.toObject(DonaturDana.class);
                    String name = d.getNamaDonatur() != null ? d.getNamaDonatur() : "Anonim";
                    if (aggregated.containsKey(name)) {
                        DonaturDana existing = aggregated.get(name);
                        existing.setNominal(existing.getNominal() + d.getNominal());
                    } else {
                        DonaturDana copy = new DonaturDana();
                        copy.setNamaDonatur(name);
                        copy.setNominal(d.getNominal());
                        aggregated.put(name, copy);
                    }
                }

                List<DonaturDana> sorted = new ArrayList<>(aggregated.values());
                Collections.sort(sorted, (d1, d2) -> Long.compare(d2.getNominal(), d1.getNominal()));

                topDonaturList.clear();
                for (int i = 0; i < Math.min(sorted.size(), 3); i++) {
                    topDonaturList.add(sorted.get(i));
                }
                topDonaturAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadRunningPrograms() {
        db.collection("programs")
                .whereEqualTo("status", "Aktif")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        runningPrograms.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Program p = doc.toObject(Program.class);
                            p.setId(doc.getId());
                            runningPrograms.add(p);
                        }
                        progressAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadRecentTransactions() {
        db.collection("donatur_dana")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        recentTransactions.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            recentTransactions.add(doc.toObject(DonaturDana.class));
                        }
                        recentTransactionsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupBottomNavigation(View view) {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_beranda);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) {
                return true;
            } else if (id == R.id.nav_program_admin) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_PageProgramFragment);
                return true;
            } else if (id == R.id.nav_statistik) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_StatistikFragment);
                return true;
            } else if (id == R.id.nav_donatur) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_DonaturAdminFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
