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
import com.aplikasiprojeksmt4.adapters.ImpactAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentHomeBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration userListener;
    private ListenerRegistration danaListener;
    private ListenerRegistration barangListener;
    private ImpactAdapter impactAdapter;
    private List<Program> impactList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        listenToUserData();
        loadUserStats();
        fetchImpactStories();

        binding.flNotification.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_NotifikasiPageFragment)
        );

        binding.btnDonasiUang.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiUangFragment)
        );

        binding.btnDonasiBarang.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiBarangFragment)
        );

        // Tombol Donasi Sekarang di Banner Hijau (Diarahkan ke Smart Matching)
        if (binding.btnDonasiSekarangHijau != null) {
            binding.btnDonasiSekarangHijau.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_SmartMatchingFragment)
            );
        }

        // Tombol Aksi Cepat: Donasi Barang (Diarahkan ke Smart Matching)
        binding.btnDonasiBarang.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_SmartMatchingFragment)
        );
    }

    private void setupRecyclerView() {
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

    private void loadUserStats() {
        String userId = auth.getUid();
        if (userId == null) return;

        danaListener = db.collection("donatur_dana").whereEqualTo("userId", userId)
                .addSnapshotListener((danaDocs, e) -> {
                    barangListener = db.collection("donatur_barang").whereEqualTo("userId", userId)
                            .addSnapshotListener((barangDocs, e2) -> {
                                if (binding == null || !isAdded()) return;
                                
                                long totalDonasi = 0;
                                long totalPenerima = 0;
                                double totalSampah = 0;

                                if (danaDocs != null) {
                                    totalDonasi += danaDocs.size();
                                    for (QueryDocumentSnapshot doc : danaDocs) {
                                        if ("Berhasil".equalsIgnoreCase(doc.getString("status"))) {
                                            totalPenerima += 5; // Assumption based on Imagen 1
                                        }
                                    }
                                }

                                if (barangDocs != null) {
                                    totalDonasi += barangDocs.size();
                                    for (QueryDocumentSnapshot doc : barangDocs) {
                                        if ("Diverifikasi".equalsIgnoreCase(doc.getString("status"))) {
                                            totalSampah += 3.7; // Assumption: 3.7kg per item donation
                                            totalPenerima += 8; 
                                        }
                                    }
                                }

                                binding.tvStatSampah.setText(String.format("%.1f kg", totalSampah));
                                binding.tvStatDonasi.setText(String.valueOf(totalDonasi));
                                binding.tvStatPenerima.setText(String.valueOf(totalPenerima));
                            });
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
                        for (QueryDocumentSnapshot doc : value) {
                            Program program = doc.toObject(Program.class);
                            program.setId(doc.getId());
                            impactList.add(program);
                        }
                        if (impactList.isEmpty()) fetchRecentImpactPrograms();
                        else impactAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void fetchRecentImpactPrograms() {
        db.collection("programs")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (binding == null || !isAdded()) return;
                    impactList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Program program = doc.toObject(Program.class);
                        program.setId(doc.getId());
                        impactList.add(program);
                    }
                    impactAdapter.notifyDataSetChanged();
                });
    }

    private void listenToUserData() {
        String userId = auth.getUid();
        if (userId == null) return;

        userListener = db.collection("users").document(userId).addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;
            if (value != null && value.exists()) {
                String nama = value.getString("nama");
                binding.tvUserName.setText(nama != null ? nama : "User");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) userListener.remove();
        if (danaListener != null) danaListener.remove();
        if (barangListener != null) barangListener.remove();
        binding = null;
    }
}
