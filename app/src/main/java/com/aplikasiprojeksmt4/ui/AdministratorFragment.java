package com.aplikasiprojeksmt4.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.BuildConfig;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.ProgramAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentAdministratorBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministratorFragment extends Fragment {

    private FragmentAdministratorBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private boolean isUploading = false;
    private String cloudVersion = "Unknown";
    private long cloudVersionCode = 0;
    
    private ProgramAdapter homeAdapter;
    private ProgramAdapter listAdapter;
    private List<Program> programList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdministratorBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup Bottom Navigation untuk Administrator
        setupBottomNavigation();

        // Tombol Status Cloud di Pojok Kanan Atas (Beranda Tab)
        binding.btnCloudStatus.setOnClickListener(v -> showCloudStatusDialog());

        // Tombol Rilis Versi Ini (Beranda Tab)
        binding.llRilisUpdate.setOnClickListener(v -> {
            if (!isUploading) {
                showReleaseConfirmation();
            }
        });

        // Tombol Tambah Program di Header
        if (binding.btnTambahProgramHeader != null) {
            binding.btnTambahProgramHeader.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_AdministratorFragment_to_TambahProgramFragment)
            );
        }

        // Muat informasi stats dan versi awal
        loadStats();
        loadCloudVersionInfo();
        loadPrograms();
    }

    private void setupRecyclerViews() {
        if (binding.rvProgramHome != null) {
            homeAdapter = new ProgramAdapter(programList);
            binding.rvProgramHome.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvProgramHome.setAdapter(homeAdapter);
        }

        if (binding.rvDaftarProgram != null) {
            listAdapter = new ProgramAdapter(programList);
            binding.rvDaftarProgram.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvDaftarProgram.setAdapter(listAdapter);
        }
    }

    private void setupBottomNavigation() {
        binding.adminBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            // Sembunyikan semua layout tab terlebih dahulu
            if (binding.layoutAdminHome != null) binding.layoutAdminHome.setVisibility(View.GONE);
            if (binding.layoutAdminProgram != null) binding.layoutAdminProgram.setVisibility(View.GONE);
            if (binding.layoutAdminStatistik != null) binding.layoutAdminStatistik.setVisibility(View.GONE);
            if (binding.layoutAdminDonatur != null) binding.layoutAdminDonatur.setVisibility(View.GONE);

            // Tampilkan layout sesuai tab yang dipilih
            if (id == R.id.adminHome) {
                if (binding.layoutAdminHome != null) binding.layoutAdminHome.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.adminProgram) {
                if (binding.layoutAdminProgram != null) binding.layoutAdminProgram.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.adminStatistik) {
                if (binding.layoutAdminStatistik != null) binding.layoutAdminStatistik.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.adminDonatur) {
                if (binding.layoutAdminDonatur != null) binding.layoutAdminDonatur.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }

    private void loadPrograms() {
        db.collection("programs")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null && binding != null) {
                        programList.clear();
                        List<Program> docs = value.toObjects(Program.class);
                        for (int i = 0; i < docs.size(); i++) {
                            Program p = docs.get(i);
                            p.setId(value.getDocuments().get(i).getId());
                            programList.add(p);
                        }
                        if (homeAdapter != null) homeAdapter.notifyDataSetChanged();
                        if (listAdapter != null) listAdapter.notifyDataSetChanged();
                        
                        if (binding.tvTotalProgramAktif != null) {
                            binding.tvTotalProgramAktif.setText(String.valueOf(programList.size()));
                        }
                    }
                });
    }

    private void loadStats() {
        if (binding != null && binding.tvTotalDana != null) {
            binding.tvTotalDana.setText("Rp 300rb");
        }
    }

    private void loadCloudVersionInfo() {
        db.collection("app_settings").document("update_info")
                .addSnapshotListener((value, error) -> {
                    if (binding == null || !isAdded()) return;
                    if (value != null && value.exists()) {
                        cloudVersion = value.getString("latest_version");
                        Long code = value.getLong("latest_version_code");
                        cloudVersionCode = (code != null) ? code : 0;
                        
                        if (BuildConfig.VERSION_CODE >= cloudVersionCode) {
                            binding.ivCloudStatus.setAlpha(1.0f);
                        } else {
                            binding.ivCloudStatus.setAlpha(0.5f);
                        }
                    }
                });
    }

    private void showCloudStatusDialog() {
        String msg = "Versi Aplikasi Saat Ini: " + BuildConfig.VERSION_NAME + " (Code: " + BuildConfig.VERSION_CODE + ")" +
                     "\nVersi di Cloud: " + cloudVersion + " (Code: " + cloudVersionCode + ")";
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Status Update Cloud")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showReleaseConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Rilis")
                .setMessage("Apakah Anda yakin ingin merilis versi " + BuildConfig.VERSION_NAME + " ke cloud?")
                .setPositiveButton("Ya, Rilis Full", (dialog, which) -> uploadApkAndRelease())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setUploadState(boolean uploading) {
        isUploading = uploading;
        if (binding == null) return;

        if (uploading) {
            binding.llRilisUpdate.setEnabled(false);
            binding.pbUpload.setVisibility(View.VISIBLE);
            binding.ivArrowRilis.setVisibility(View.GONE);
            binding.tvRilisText.setText("Proses...");
        } else {
            binding.llRilisUpdate.setEnabled(true);
            binding.pbUpload.setVisibility(View.GONE);
            binding.ivArrowRilis.setVisibility(View.VISIBLE);
            binding.tvRilisText.setText("Rilis\nVersi Ini");
        }
    }

    private void uploadApkAndRelease() {
        if (getContext() == null) return;
        
        setUploadState(true);
        Toast.makeText(getContext(), "Sedang mengunggah full APK...", Toast.LENGTH_SHORT).show();
        
        String apkPath = getContext().getPackageCodePath();
        File apkFile = new File(apkPath);
        Uri fileUri = Uri.fromFile(apkFile);

        // Gunakan timestamp agar file di storage unik atau timpa yang lama
        StorageReference storageRef = storage.getReference().child("releases/donasiku_v" + BuildConfig.VERSION_CODE + ".apk");

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateFirestoreInfo(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    setUploadState(false);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateFirestoreInfo(String downloadUrl) {
        String currentVersion = BuildConfig.VERSION_NAME;
        int currentVersionCode = BuildConfig.VERSION_CODE;
        
        Map<String, Object> update = new HashMap<>();
        update.put("latest_version", currentVersion);
        update.put("latest_version_code", currentVersionCode);
        update.put("download_url", downloadUrl);
        update.put("updated_at", System.currentTimeMillis());
        
        db.collection("app_settings").document("update_info")
                .set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    setUploadState(false);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Berhasil Rilis Versi " + currentVersion + " ke Cloud", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setUploadState(false);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal update Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
