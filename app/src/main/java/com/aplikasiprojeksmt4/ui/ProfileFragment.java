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

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentProfileBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration userListener;
    private ListenerRegistration statsDanaListener;
    private ListenerRegistration statsBarangListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();
        loadUserStats();

        binding.llRiwayat.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HistoryFragment)
        );

        binding.llDataDiri.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_DataDiriFragment)
        );

        binding.llLaporanRealTime.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_LaporanRealTimeFragment)
        );

        binding.llSertifikat.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_SertifikatFragment)
        );

        binding.llAdministrator.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HomepageAdminFragment)
        );

        binding.llManajemenMitra.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_ManajemenProgramFragment)
        );
        
        binding.llKebijakanPrivasi.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_KebijakanPrivasiFragment)
        );

        binding.llBantuan.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_FaqFragment)
        );

        binding.llDaftarMitra.setOnClickListener(v -> showDaftarMitraDialog());

        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();
            sessionManager.logout();
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_WelcomeFragment);
        });
    }

    private void loadUserData() {
        String userId = auth.getUid();
        if (userId == null) return;

        userListener = db.collection("users").document(userId).addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;

            if (value != null && value.exists()) {
                String nama = value.getString("nama");
                String email = value.getString("email");
                String photoUrl = value.getString("profile_photo");
                String role = value.getString("role");

                binding.tvProfileName.setText(nama != null ? nama : "User");
                binding.tvProfileEmail.setText(email != null ? email : "");

                // Role UI
                if ("admin".equals(role)) {
                    binding.llAdministrator.setVisibility(View.VISIBLE);
                    binding.viewSeparatorAdmin.setVisibility(View.VISIBLE);
                    binding.llManajemenMitra.setVisibility(View.VISIBLE);
                    binding.viewSeparatorMitra.setVisibility(View.VISIBLE);
                    binding.llDaftarMitra.setVisibility(View.GONE);
                    binding.viewSeparatorDaftarMitra.setVisibility(View.GONE);
                } else if ("mitra".equals(role)) {
                    binding.llAdministrator.setVisibility(View.GONE);
                    binding.viewSeparatorAdmin.setVisibility(View.GONE);
                    binding.llManajemenMitra.setVisibility(View.VISIBLE);
                    binding.viewSeparatorMitra.setVisibility(View.VISIBLE);
                    binding.llDaftarMitra.setVisibility(View.GONE);
                    binding.viewSeparatorDaftarMitra.setVisibility(View.GONE);
                } else {
                    // Default Role: User
                    binding.llAdministrator.setVisibility(View.GONE);
                    binding.viewSeparatorAdmin.setVisibility(View.GONE);
                    binding.llManajemenMitra.setVisibility(View.GONE);
                    binding.viewSeparatorMitra.setVisibility(View.GONE);
                    binding.llDaftarMitra.setVisibility(View.VISIBLE);
                    binding.viewSeparatorDaftarMitra.setVisibility(View.VISIBLE);
                }

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(this).load(photoUrl).circleCrop().placeholder(R.drawable.group_2).into(binding.ivProfilePicture);
                }
            }
        });
    }

    private void loadUserStats() {
        String userId = auth.getUid();
        if (userId == null) return;

        statsDanaListener = db.collection("donatur_dana").whereEqualTo("userId", userId)
                .addSnapshotListener((danaDocs, e) -> {
                    if (binding == null || !isAdded()) return;
                    statsBarangListener = db.collection("donatur_barang").whereEqualTo("userId", userId)
                            .addSnapshotListener((barangDocs, e2) -> {
                                if (binding == null || !isAdded()) return;
                                
                                long totalDana = 0;
                                long totalBarang = 0;
                                long totalTersalur = 0;

                                if (danaDocs != null) {
                                    for (QueryDocumentSnapshot doc : danaDocs) {
                                        Long nominal = doc.getLong("nominal");
                                        if (nominal != null) totalDana += nominal;
                                        String status = doc.getString("status");
                                        if ("Berhasil".equalsIgnoreCase(status) || "Success".equalsIgnoreCase(status)) {
                                            totalTersalur++;
                                        }
                                    }
                                }

                                if (barangDocs != null) {
                                    for (QueryDocumentSnapshot doc : barangDocs) {
                                        String status = doc.getString("status");
                                        if ("Diverifikasi".equals(status)) {
                                            totalBarang++;
                                            totalTersalur++;
                                        }
                                    }
                                }

                                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                                nf.setMaximumFractionDigits(0);
                                binding.tvProfileTotalDana.setText(nf.format(totalDana));
                                binding.tvProfileTotalBarang.setText(String.valueOf(totalBarang));
                                binding.tvProfileTotalTerbantu.setText(String.valueOf(totalTersalur));
                            });
                });
    }

    private void showDaftarMitraDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Daftar Jadi Mitra")
                .setMessage("Apakah Anda ingin mengajukan diri sebagai mitra DonasiKu?")
                .setPositiveButton("Ya, Ajukan", (dialog, which) -> daftarJadiMitra())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void daftarJadiMitra() {
        String userId = auth.getUid();
        if (userId == null) return;

        db.collection("users").document(userId)
                .update("role", "mitra")
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Berhasil menjadi mitra!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) userListener.remove();
        if (statsDanaListener != null) statsDanaListener.remove();
        if (statsBarangListener != null) statsBarangListener.remove();
        binding = null;
    }
}
