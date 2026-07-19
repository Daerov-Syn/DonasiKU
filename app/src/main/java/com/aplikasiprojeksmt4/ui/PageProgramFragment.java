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
import com.aplikasiprojeksmt4.adapters.ProgramAdapter;
import com.aplikasiprojeksmt4.databinding.PageProgramBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PageProgramFragment extends Fragment {

    private PageProgramBinding binding;
    private FirebaseFirestore db;
    private ProgramAdapter adapter;
    private List<Program> programList = new ArrayList<>();
    private String currentFilter = "Dana"; // Default filter
    private ListenerRegistration registration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupTabs();
        setupRecyclerView();
        loadPrograms();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_program_admin);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) {
                Navigation.findNavController(requireView()).navigate(R.id.action_PageProgramFragment_to_HomepageAdminFragment);
                return true;
            } else if (id == R.id.nav_program_admin) {
                return true;
            } else if (id == R.id.nav_statistik) {
                Navigation.findNavController(requireView()).navigate(R.id.action_PageProgramFragment_to_StatistikFragment);
                return true;
            }
            return false;
        });
    }

    private void setupTabs() {
        binding.tabDana.setOnClickListener(v -> updateFilter("Dana"));
        binding.tabBarang.setOnClickListener(v -> updateFilter("Barang"));
        binding.tabSelesai.setOnClickListener(v -> updateFilter("Selesai"));
    }

    private void updateFilter(String filter) {
        if (currentFilter.equals(filter)) return;
        currentFilter = filter;
        updateTabUI();
        loadPrograms();
    }

    private void updateTabUI() {
        int inactiveBg = R.drawable.bg_tab_inactive;
        int activeBg = R.drawable.bg_tab_active;
        int inactiveText = ContextCompat.getColor(requireContext(), R.color.primary_purple);
        int activeText = ContextCompat.getColor(requireContext(), R.color.white);

        binding.tabDana.setBackgroundResource(inactiveBg);
        binding.tabDana.setTextColor(inactiveText);
        binding.tabBarang.setBackgroundResource(inactiveBg);
        binding.tabBarang.setTextColor(inactiveText);
        binding.tabSelesai.setBackgroundResource(inactiveBg);
        binding.tabSelesai.setTextColor(inactiveText);

        if (currentFilter.equals("Dana")) {
            binding.tabDana.setBackgroundResource(activeBg);
            binding.tabDana.setTextColor(activeText);
        } else if (currentFilter.equals("Barang")) {
            binding.tabBarang.setBackgroundResource(activeBg);
            binding.tabBarang.setTextColor(activeText);
        } else if (currentFilter.equals("Selesai")) {
            binding.tabSelesai.setBackgroundResource(activeBg);
            binding.tabSelesai.setTextColor(activeText);
        }
    }

    private void setupRecyclerView() {
        adapter = new ProgramAdapter(programList);
        adapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(requireView()).navigate(R.id.action_PageProgramFragment_to_DetailDonasiMitraFragment, bundle);
        });
        binding.rvProgram.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProgram.setAdapter(adapter);
    }

    private void loadPrograms() {
        if (registration != null) {
            registration.remove();
        }

        Query query = db.collection("programs");

        if (currentFilter.equals("Selesai")) {
            query = query.whereEqualTo("status", "Selesai");
        } else {
            query = query.whereEqualTo("tipe", currentFilter)
                         .whereNotEqualTo("status", "Selesai");
        }

        registration = query.addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                programList.clear();
                for (int i = 0; i < value.getDocuments().size(); i++) {
                    Program p = value.getDocuments().get(i).toObject(Program.class);
                    if (p != null) {
                        p.setId(value.getDocuments().get(i).getId());
                        programList.add(p);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (registration != null) {
            registration.remove();
        }
        binding = null;
    }
}
