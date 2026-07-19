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
import com.aplikasiprojeksmt4.databinding.FragmentHistoryUangBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryUangFragment extends Fragment {
    private FragmentHistoryUangBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private HistoryUangAdapter adapter;
    private List<DonaturDana> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryUangBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        binding.tabSemua.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryUangFragment_to_HistoryFragment));
        
        binding.tabBarang.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryUangFragment_to_HistoryBarangFragment));
        
        binding.tabProses.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryUangFragment_to_HistoryProsesFragment));

        setupRecyclerView();
        loadHistory();
    }

    private void setupRecyclerView() {
        adapter = new HistoryUangAdapter(historyList);
        binding.rvHistoryUang.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistoryUang.setAdapter(adapter);
    }

    private void loadHistory() {
        String userId = auth.getUid();
        if (userId == null) return;

        db.collection("donatur_dana")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("HistoryUang", "Gagal memuat riwayat: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        historyList.clear();
                        long totalNominal = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            DonaturDana d = doc.toObject(DonaturDana.class);
                            historyList.add(d);
                            totalNominal += d.getNominal();
                        }
                        
                        adapter.notifyDataSetChanged();
                        updateStats(totalNominal, historyList.size());
                        
                        if (historyList.isEmpty()) {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                            binding.rvHistoryUang.setVisibility(View.GONE);
                        } else {
                            binding.tvEmpty.setVisibility(View.GONE);
                            binding.rvHistoryUang.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void updateStats(long total, int count) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        binding.tvTotalDonasi.setText(formatter.format(total));
        binding.tvCountDonasi.setText(String.valueOf(count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
