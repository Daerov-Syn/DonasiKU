package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.TopDonaturAdapter;
import com.aplikasiprojeksmt4.databinding.PageDaftarDonaturMitraBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarDonaturMitraFragment extends Fragment {

    private PageDaftarDonaturMitraBinding binding;
    private final List<DonaturDana> allDonaturList = new ArrayList<>();
    private final List<DonaturDana> displayedList = new ArrayList<>();
    private TopDonaturAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageDaftarDonaturMitraBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new TopDonaturAdapter(displayedList);
        binding.rvSemuaDonatur.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSemuaDonatur.setAdapter(adapter);

        loadDonaturs();

        binding.btnSortTerbaru.setOnClickListener(v -> sortData(true));
        binding.btnSortTerbesar.setOnClickListener(v -> sortData(false));

        binding.etSearchDonatur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDonaturs() {
        String userId = auth.getUid();
        if (userId == null) return;

        db.collection("programs").whereEqualTo("dibuat_oleh", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> programIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        programIds.add(doc.getId());
                    }

                    if (programIds.isEmpty()) return;

                    db.collection("donatur_dana").whereIn("programId", programIds)
                            .addSnapshotListener((danaValue, e1) -> {
                                db.collection("donatur_barang").whereIn("programId", programIds)
                                        .addSnapshotListener((barangValue, e2) -> {
                                            if (binding == null || !isAdded()) return;
                                            
                                            Map<String, DonaturDana> aggregated = new HashMap<>();
                                            
                                            if (danaValue != null) {
                                                for (QueryDocumentSnapshot doc : danaValue) {
                                                    processDoc(aggregated, doc, true);
                                                }
                                            }
                                            
                                            if (barangValue != null) {
                                                for (QueryDocumentSnapshot doc : barangValue) {
                                                    processDoc(aggregated, doc, false);
                                                }
                                            }

                                            allDonaturList.clear();
                                            allDonaturList.addAll(aggregated.values());
                                            
                                            updateTop3UI(allDonaturList);
                                            sortData(true);
                                        });
                            });
                });
    }

    private void processDoc(Map<String, DonaturDana> map, QueryDocumentSnapshot doc, boolean isDana) {
        String name = doc.getString("namaDonatur");
        if (name == null) name = "Anonim";
        
        long nominal = 0;
        if (isDana && doc.contains("nominal") && doc.get("nominal") != null) {
            nominal = doc.getLong("nominal");
        }

        if (map.containsKey(name)) {
            DonaturDana existing = map.get(name);
            existing.setNominal(existing.getNominal() + nominal);
            existing.setJumlahTransaksi(existing.getJumlahTransaksi() + 1);
            existing.setJumlahKategori(existing.getJumlahKategori() + 1);
        } else {
            DonaturDana d = new DonaturDana();
            d.setNamaDonatur(name);
            d.setNominal(nominal);
            d.setJumlahTransaksi(1);
            d.setJumlahKategori(1);
            map.put(name, d);
        }
    }

    private void updateTop3UI(List<DonaturDana> list) {
        List<DonaturDana> sorted = new ArrayList<>(list);
        Collections.sort(sorted, (d1, d2) -> Long.compare(d2.getNominal(), d1.getNominal()));

        if (!sorted.isEmpty()) {
            DonaturDana d = sorted.get(0);
            binding.tvTop1Nama.setText(d.getNamaDonatur());
            binding.tvTop1Nominal.setText("Rp " + formatNominal(d.getNominal()));
            binding.tvTop1Inisial.setText(d.getNamaDonatur().substring(0, Math.min(2, d.getNamaDonatur().length())).toUpperCase());
        }
        if (sorted.size() >= 2) {
            DonaturDana d = sorted.get(1);
            binding.tvTop2Nama.setText(d.getNamaDonatur());
            binding.tvTop2Nominal.setText("Rp " + formatNominal(d.getNominal()));
            binding.tvTop2Inisial.setText(d.getNamaDonatur().substring(0, Math.min(2, d.getNamaDonatur().length())).toUpperCase());
        }
        if (sorted.size() >= 3) {
            DonaturDana d = sorted.get(2);
            binding.tvTop3Nama.setText(d.getNamaDonatur());
            binding.tvTop3Nominal.setText("Rp " + formatNominal(d.getNominal()));
            binding.tvTop3Inisial.setText(d.getNamaDonatur().substring(0, Math.min(2, d.getNamaDonatur().length())).toUpperCase());
        }
    }

    private String formatNominal(long nominal) {
        if (nominal >= 1000000) return (nominal / 1000000) + "jt";
        if (nominal >= 1000) return (nominal / 1000) + "rb";
        return String.valueOf(nominal);
    }

    private void sortData(boolean terbaru) {
        if (terbaru) {
            binding.btnSortTerbaru.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.primary_purple, null));
            binding.btnSortTerbaru.setTextColor(android.graphics.Color.WHITE);
            binding.btnSortTerbesar.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), android.R.color.white, null));
            binding.btnSortTerbesar.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_purple));
            Collections.sort(allDonaturList, (d1, d2) -> Integer.compare(d2.getJumlahTransaksi(), d1.getJumlahTransaksi()));
        } else {
            binding.btnSortTerbesar.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.primary_purple, null));
            binding.btnSortTerbesar.setTextColor(android.graphics.Color.WHITE);
            binding.btnSortTerbaru.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), android.R.color.white, null));
            binding.btnSortTerbaru.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_purple));
            Collections.sort(allDonaturList, (d1, d2) -> Long.compare(d2.getNominal(), d1.getNominal()));
        }
        displayedList.clear();
        displayedList.addAll(allDonaturList);
        adapter.notifyDataSetChanged();
    }

    private void filterSearch(String query) {
        displayedList.clear();
        if (query.isEmpty()) {
            displayedList.addAll(allDonaturList);
        } else {
            for (DonaturDana d : allDonaturList) {
                if (d.getNamaDonatur().toLowerCase().contains(query.toLowerCase())) {
                    displayedList.add(d);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
