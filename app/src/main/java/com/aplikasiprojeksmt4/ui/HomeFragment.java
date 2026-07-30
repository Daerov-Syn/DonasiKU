package com.aplikasiprojeksmt4.ui;

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
import com.aplikasiprojeksmt4.adapters.ImpactAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentHomeBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;
    private ImpactAdapter impactAdapter;
    private List<Program> impactList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        listenToUserData();
        fetchImpactStories();

        // Navigasi ke halaman notifikasi
        binding.flNotification.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_NotifikasiPageFragment)
        );

        // Navigasi ke halaman Donasi Uang
        binding.btnDonasiUang.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiUangFragment)
        );

        // Navigasi ke halaman Donasi Barang
        binding.btnDonasiBarang.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiBarangFragment)
        );

        // Tombol Donasi Sekarang di Banner Hijau (Diarahkan ke Donasi Barang)
        if (binding.btnDonasiSekarangHijau != null) {
            binding.btnDonasiSekarangHijau.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiBarangFragment)
            );
        }

        // Lihat Semua Kisah Dampak
        binding.tvLihatSemuaImpact.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_ProgramPageFragment);
        });
    }

    private void setupRecyclerView() {
        // Setup Impact RecyclerView (Kisah Dampak)
        impactAdapter = new ImpactAdapter(impactList);
        binding.rvImpact.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvImpact.setAdapter(impactAdapter);
        binding.rvImpact.setNestedScrollingEnabled(false);

        impactAdapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(requireView()).navigate(R.id.action_HomeFragment_to_DetailProgramFragment, bundle);
        });
    }

    private void fetchImpactStories() {
        db.collection("programs")
                .whereEqualTo("status", "Selesai")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        impactList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            Program program = doc.toObject(Program.class);
                            if (program != null) {
                                program.setId(doc.getId());
                                impactList.add(program);
                            }
                        }

                        if (impactList.isEmpty()) {
                            fetchRecentImpactPrograms();
                        } else {
                            impactAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void fetchRecentImpactPrograms() {
        db.collection("programs")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    impactList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        Program program = doc.toObject(Program.class);
                        if (program != null) {
                            program.setId(doc.getId());
                            impactList.add(program);
                        }
                    }
                    impactAdapter.notifyDataSetChanged();
                });
    }

    private void listenToUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (binding != null) {
                binding.tvUserName.setText(sessionManager.getUsername());
            }
            return;
        }

        userListener = db.collection("users").document(userId).addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;

            if (value != null && value.exists()) {
                String nama = value.getString("nama");
                // Set nama pengguna, foto profil sudah dihapus dari desain
                binding.tvUserName.setText(nama != null ? nama : "User");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) {
            userListener.remove();
        }
        binding = null;
    }
}