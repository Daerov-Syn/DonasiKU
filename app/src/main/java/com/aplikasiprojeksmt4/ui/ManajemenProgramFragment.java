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
import com.aplikasiprojeksmt4.adapters.MitraNotificationAdapter;
import com.aplikasiprojeksmt4.models.Notification;
import android.graphics.Color;
import com.aplikasiprojeksmt4.adapters.ProgramAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentManajemenProgramBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManajemenProgramFragment extends Fragment {

    private FragmentManajemenProgramBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    private ProgramAdapter berandaAdapter;
    private List<Program> activePrograms = new ArrayList<>();
    
    private ProgramAdapter semuaProgramAdapter;
    private List<Program> allPrograms = new ArrayList<>();

    private MitraNotificationAdapter notifAdapter;
    private List<Notification> notificationList = new ArrayList<>();

    private ListenerRegistration programsListener;
    private ListenerRegistration notifDanaListener;
    private ListenerRegistration notifBarangListener;

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
        
        setupRecyclerViews();
        setupBottomNavigation();
        loadDashboardHeader();
        listenToUserPrograms();

        binding.btnBuatProgram.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_TambahProgramFragment)
        );

        binding.btnBuatProgramSecondary.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_TambahProgramFragment)
        );
        
        binding.btnTarikDanaMenu.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_TarikDanaFragment)
        );

        binding.btnMenuDonatur.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ManajemenProgram_to_DaftarDonatur)
        );
    }

    private void setupRecyclerViews() {
        // Adapter untuk Beranda (Program Aktif)
        berandaAdapter = new ProgramAdapter(activePrograms);
        berandaAdapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(requireView()).navigate(R.id.action_ManajemenProgramFragment_to_DetailDonasiMitraFragment, bundle);
        });
        binding.rvProgramBerjalan.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProgramBerjalan.setAdapter(berandaAdapter);

        // Adapter untuk Tab Program (Semua Program)
        semuaProgramAdapter = new ProgramAdapter(allPrograms);
        semuaProgramAdapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(requireView()).navigate(R.id.action_ManajemenProgramFragment_to_DetailDonasiMitraFragment, bundle);
        });
        binding.rvSemuaProgramMitra.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSemuaProgramMitra.setAdapter(semuaProgramAdapter);

        // Notifikasi
        notifAdapter = new MitraNotificationAdapter(notificationList);
        binding.rvNotifikasiMitra.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifikasiMitra.setAdapter(notifAdapter);
    }

    private void loadDashboardHeader() {
        String userId = auth.getUid();
        if (userId == null) return;

        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (binding != null && isAdded() && doc.exists()) {
                        String name = doc.getString("nama");
                        if (name != null) binding.tvName.setText(name);
                    }
                });
    }

    private void listenToUserPrograms() {
        String userId = auth.getUid();
        if (userId == null) return;

        programsListener = db.collection("programs")
                .whereEqualTo("dibuat_oleh", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (binding == null || !isAdded()) return;
                    if (error != null) {
                        Log.e("ManajemenFragment", "Error sync programs", error);
                        return;
                    }

                    if (value != null) {
                        activePrograms.clear();
                        allPrograms.clear();
                        long totalDana = 0;
                        int totalDonatur = 0;
                        int totalPenerima = 0;
                        int selesaiCount = 0;
                        
                        for (QueryDocumentSnapshot doc : value) {
                            Program p = doc.toObject(Program.class);
                            p.setId(doc.getId());
                            
                            allPrograms.add(p);
                            
                            if ("Aktif".equalsIgnoreCase(p.getStatus())) {
                                activePrograms.add(p);
                                totalDana += p.getTerkumpul();
                                totalDonatur += p.getDonatur_count();
                                totalPenerima += p.getPenerima_count();
                            } else if ("Selesai".equalsIgnoreCase(p.getStatus())) {
                                selesaiCount++;
                            }
                        }
                        
                        // Update UI Beranda
                        if (activePrograms.isEmpty()) {
                            binding.layoutEmptyState.setVisibility(View.VISIBLE);
                            binding.rvProgramBerjalan.setVisibility(View.GONE);
                        } else {
                            binding.layoutEmptyState.setVisibility(View.GONE);
                            binding.rvProgramBerjalan.setVisibility(View.VISIBLE);
                            berandaAdapter.notifyDataSetChanged();
                        }

                        // Update UI Tab Program
                        semuaProgramAdapter.notifyDataSetChanged();
                        binding.tvTotalProgram.setText(String.valueOf(allPrograms.size()));
                        binding.tvTotalAktif.setText(String.valueOf(activePrograms.size()));
                        binding.tvTotalSelesai.setText(String.valueOf(selesaiCount));

                        // Update Stats Header
                        updateStatsUI(totalDana, totalDonatur, totalPenerima);
                        
                        // Load Notifications after we have program IDs
                        loadMitraNotifications(allPrograms);
                    }
                });
    }

    private void loadMitraNotifications(List<Program> myPrograms) {
        if (myPrograms.isEmpty()) return;
        List<String> programIds = new ArrayList<>();
        for (Program p : myPrograms) programIds.add(p.getId());

        // Dana Notifs
        notifDanaListener = db.collection("donatur_dana")
                .whereIn("programId", programIds)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener((value, error) -> {
                    if (value != null) processDonationDocs(value, "Uang");
                });

        // Barang Notifs
        notifBarangListener = db.collection("donatur_barang")
                .whereIn("programId", programIds)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener((value, error) -> {
                    if (value != null) processDonationDocs(value, "Barang");
                });
    }

    private void processDonationDocs(com.google.firebase.firestore.QuerySnapshot value, String type) {
        // Simple logic: add to list and sort
        for (QueryDocumentSnapshot doc : value) {
            String name = doc.getString("namaDonatur");
            if (name == null) name = "Anonim";
            String progId = doc.getString("programId");
            
            String title = type.equals("Uang") ? "Donasi Baru Masuk! 💜" : "Donasi Barang Masuk! 📦";
            String desc = name + " baru saja berdonasi untuk program Anda.";

            notificationList.add(0, new Notification(
                    doc.getId(),
                    title,
                    desc,
                    "Baru saja",
                    android.R.drawable.ic_menu_myplaces,
                    Color.parseColor("#FCE4EC"),
                    Color.parseColor("#E91E63"),
                    progId // Pass programId for navigation
            ));
        }
        notifAdapter.notifyDataSetChanged();
    }

    private void updateStatsUI(long totalDana, int donatur, int penerima) {
        String formattedDana = "Rp. " + java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID")).format(totalDana);
        binding.tvMainAmount.setText(formattedDana);
        binding.tvTotalDonatur.setText(String.valueOf(donatur));
        binding.tvTotalPenerima.setText(String.valueOf(penerima));
        
        // Sisa Hari logic can be added if needed, for now use active program count as placeholder or just 0
        binding.tvProgramAktifCount.setText(String.valueOf(activePrograms.size()));
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
        if (programsListener != null) programsListener.remove();
        if (notifDanaListener != null) notifDanaListener.remove();
        if (notifBarangListener != null) notifBarangListener.remove();
        binding = null;
    }
}
