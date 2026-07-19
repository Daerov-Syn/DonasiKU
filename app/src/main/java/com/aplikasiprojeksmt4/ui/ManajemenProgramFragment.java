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
import com.aplikasiprojeksmt4.adapters.ProgramAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentManajemenProgramBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManajemenProgramFragment extends Fragment {

    private FragmentManajemenProgramBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgramAdapter programAdapter;
    private List<Program> programList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentManajemenProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        setupBottomNavigation();
        loadUserPrograms();
        loadDashboardHeader();

        // Navigasi ke Tambah Program
        binding.btnBuatProgram.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_TambahProgramFragment)
        );
        
        // Navigasi ke Tarik Dana dari Menu Grid
        binding.btnTarikDanaMenu.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_TarikDanaFragment)
        );
        
        if (binding.btnBuatProgramMenu != null) {
            binding.btnBuatProgramMenu.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_TambahProgramFragment)
            );
        }
    }

    private void setupRecyclerView() {
        programAdapter = new ProgramAdapter(programList);
        programAdapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(requireView()).navigate(R.id.action_ManajemenProgramFragment_to_DetailDonasiMitraFragment, bundle);
        });

        binding.rvProgramBerjalan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProgramBerjalan.setAdapter(programAdapter);
        
        binding.rvSemuaProgramMitra.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSemuaProgramMitra.setAdapter(programAdapter);
    }

    private void loadDashboardHeader() {
        String userId = auth.getUid();
        if (userId == null) return;

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String fotoUrl = documentSnapshot.getString("fotoUrl");
                        if (fotoUrl == null) fotoUrl = documentSnapshot.getString("foto");

                        if (name != null && !name.isEmpty()) {
                            binding.tvName.setText(name);
                        }

                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            loadProfileImage(fotoUrl);
                        } else if (auth.getCurrentUser() != null && auth.getCurrentUser().getPhotoUrl() != null) {
                            loadProfileImage(auth.getCurrentUser().getPhotoUrl().toString());
                        } else {
                            binding.ivProfileImage.setImageResource(R.drawable.logo);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ManajemenFragment", "Gagal load header dari users", e));
    }

    private void loadProfileImage(String url) {
        if (!isAdded()) return;
        Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(binding.ivProfileImage);
    }

    private void loadUserPrograms() {
        String userId = auth.getUid();
        if (userId == null) return;

        db.collection("programs")
                .whereEqualTo("dibuat_oleh", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ManajemenFragment", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        programList.clear();
                        long totalDana = 0;
                        int aktifCount = 0;
                        
                        for (QueryDocumentSnapshot doc : value) {
                            Program program = doc.toObject(Program.class);
                            program.setId(doc.getId());
                            programList.add(program);
                            
                            totalDana += program.getTerkumpul();
                            if ("Aktif".equalsIgnoreCase(program.getStatus())) {
                                aktifCount++;
                            }
                        }
                        
                        programAdapter.notifyDataSetChanged();
                        updateStatsUI(totalDana, programList.size(), aktifCount);
                    }
                });
    }

    private void updateStatsUI(long totalDana, int totalProgram, int aktifCount) {
        binding.tvMainAmount.setText("Rp. " + String.format("%,d", totalDana).replace(',', '.'));
        binding.tvProgramAktifCount.setText(String.valueOf(totalProgram));
        
        binding.tvTotalDonatur.setText("87"); 
        binding.tvTotalPenerima.setText("45");
    }

    private void setupBottomNavigation() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.layoutManajemenProfile, new ProfileMitraFragment())
                .commit();

        binding.manajemenBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            binding.layoutManajemenHome.setVisibility(View.GONE);
            binding.layoutManajemenProgram.setVisibility(View.GONE);
            binding.layoutManajemenNotif.setVisibility(View.GONE);
            binding.layoutManajemenProfile.setVisibility(View.GONE);

            if (id == R.id.nav_manajemen_home) {
                binding.layoutManajemenHome.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_manajemen_program) {
                binding.layoutManajemenProgram.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_manajemen_notif) {
                binding.layoutManajemenNotif.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_manajemen_profile) {
                binding.layoutManajemenProfile.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        binding.manajemenBottomNav.setSelectedItemId(R.id.nav_manajemen_home);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
