package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.aplikasiprojeksmt4.adapters.DonaturAdminAdapter;
import com.aplikasiprojeksmt4.databinding.DonaturAdminBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.aplikasiprojeksmt4.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonaturAdminFragment extends Fragment {

    private DonaturAdminBinding binding;
    private FirebaseFirestore db;
    private DonaturAdminAdapter adapter;
    
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private Map<String, Long> donationTotals = new HashMap<>();
    private Map<String, Integer> donationCounts = new HashMap<>();
    
    private String currentFilter = "Semua";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DonaturAdminBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadData();
        setupSearchAndFilters();
        setupBottomNavigation(view);
    }

    private void setupRecyclerView() {
        adapter = new DonaturAdminAdapter(filteredUsers, donationTotals, donationCounts);
        binding.rvDonatur.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonatur.setAdapter(adapter);
    }

    private void loadData() {
        // Fetch all users first
        db.collection("users").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                allUsers.clear();
                for (QueryDocumentSnapshot doc : value) {
                    User u = doc.toObject(User.class);
                    u.setId(doc.getId());
                    // Only show regular users, not admins
                    if (!"admin".equals(u.getRole())) {
                        allUsers.add(u);
                    }
                }
                binding.tvTotalDonatur.setText(String.valueOf(allUsers.size()));
                fetchDonationStats();
            }
        });
    }

    private void fetchDonationStats() {
        db.collection("donatur_dana").addSnapshotListener((value, error) -> {
            if (value != null) {
                donationTotals.clear();
                donationCounts.clear();
                int activeCount = 0;
                
                for (QueryDocumentSnapshot doc : value) {
                    DonaturDana d = doc.toObject(DonaturDana.class);
                    String uid = d.getUserId();
                    if (uid != null) {
                        donationTotals.put(uid, donationTotals.getOrDefault(uid, 0L) + d.getNominal());
                        donationCounts.put(uid, donationCounts.getOrDefault(uid, 0) + 1);
                    }
                }
                
                // Count active users (those who have donated at least once)
                for (User u : allUsers) {
                    if (donationCounts.getOrDefault(u.getId(), 0) > 0) {
                        activeCount++;
                    }
                }
                
                binding.tvDonaturAktif.setText(String.valueOf(activeCount));
                applyFilterAndSearch();
            }
        });
    }

    private void setupSearchAndFilters() {
        binding.etSearchDonatur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnFilterSemua.setOnClickListener(v -> {
            currentFilter = "Semua";
            updateFilterButtons();
            applyFilterAndSearch();
        });

        binding.btnFilterAktif.setOnClickListener(v -> {
            currentFilter = "Aktif";
            updateFilterButtons();
            applyFilterAndSearch();
        });

        binding.btnFilterTidakAktif.setOnClickListener(v -> {
            currentFilter = "Tidak Aktif";
            updateFilterButtons();
            applyFilterAndSearch();
        });
    }

    private void updateFilterButtons() {
        // Reset all
        binding.btnFilterSemua.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnFilterSemua.setTextColor(android.graphics.Color.parseColor("#9C27B0"));
        binding.btnFilterAktif.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnFilterAktif.setTextColor(android.graphics.Color.parseColor("#9C27B0"));
        binding.btnFilterTidakAktif.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
        binding.btnFilterTidakAktif.setTextColor(android.graphics.Color.parseColor("#9C27B0"));

        // Highlight selected
        if (currentFilter.equals("Semua")) {
            binding.btnFilterSemua.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9C27B0")));
            binding.btnFilterSemua.setTextColor(android.graphics.Color.WHITE);
        } else if (currentFilter.equals("Aktif")) {
            binding.btnFilterAktif.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9C27B0")));
            binding.btnFilterAktif.setTextColor(android.graphics.Color.WHITE);
        } else if (currentFilter.equals("Tidak Aktif")) {
            binding.btnFilterTidakAktif.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9C27B0")));
            binding.btnFilterTidakAktif.setTextColor(android.graphics.Color.WHITE);
        }
    }

    private void applyFilterAndSearch() {
        String query = binding.etSearchDonatur.getText().toString().toLowerCase();
        filteredUsers.clear();

        for (User user : allUsers) {
            boolean matchesSearch = user.getNama() != null && user.getNama().toLowerCase().contains(query);
            boolean isAktif = donationCounts.getOrDefault(user.getId(), 0) > 0;
            
            boolean matchesFilter = false;
            if (currentFilter.equals("Semua")) matchesFilter = true;
            else if (currentFilter.equals("Aktif")) matchesFilter = isAktif;
            else if (currentFilter.equals("Tidak Aktif")) matchesFilter = !isAktif;

            if (matchesSearch && matchesFilter) {
                filteredUsers.add(user);
            }
        }
        adapter.updateData(filteredUsers, donationTotals, donationCounts);
    }

    private void setupBottomNavigation(View view) {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_donatur);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_donatur) {
                return true;
            } else if (id == R.id.nav_beranda) {
                Navigation.findNavController(view).navigate(R.id.action_DonaturAdminFragment_to_HomepageAdminFragment);
                return true;
            } else if (id == R.id.nav_program_admin) {
                Navigation.findNavController(view).navigate(R.id.action_DonaturAdminFragment_to_PageProgramFragment);
                return true;
            } else if (id == R.id.nav_statistik) {
                Navigation.findNavController(view).navigate(R.id.action_DonaturAdminFragment_to_StatistikFragment);
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
