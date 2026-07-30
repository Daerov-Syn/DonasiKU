package com.aplikasiprojeksmt4.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentProfileBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration userListener;

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

        binding.llDataDiri.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_DataDiriFragment)
        );

        binding.llRiwayat.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HistoryFragment)
        );

        binding.llLaporanRealTime.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_LaporanRealTimeFragment)
        );

        // Menghubungkan ke Homepage Admin (Hanya muncul jika role admin)
        binding.llAdministrator.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HomepageAdminFragment)
        );

        // Menghubungkan ke Manajemen Mitra (Admin & Mitra)
        binding.llManajemenMitra.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_ManajemenProgramFragment)
        );

        // Fitur Daftar Jadi Mitra
        binding.llDaftarMitra.setOnClickListener(v -> showDaftarMitraDialog());

        // Fitur Baru di Bagian AKTIVITAS
        binding.llSertifikat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Buka Sertifikat Donasi", Toast.LENGTH_SHORT).show();
            // Nanti ganti dengan Navigation.findNavController(v).navigate(R.id...);
        });

        // Fitur Baru di Bagian LAINNYA
        binding.llKebijakanPrivasi.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Buka Kebijakan Privasi", Toast.LENGTH_SHORT).show();
        });

        binding.llBantuan.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Buka Bantuan & FAQ", Toast.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireActivity(), WelcomeFragment.class); 
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserData() {
        String userId = auth.getUid(); // Menggunakan UID dari Auth langsung
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

                // Logika Tampilan Berdasarkan Role
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
                    Glide.with(this)
                            .load(photoUrl)
                            .circleCrop()
                            .placeholder(R.drawable.group_2)
                            .into(binding.ivProfilePicture);
                }
            }
        });
    }

    private void showDaftarMitraDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Daftar Menjadi Mitra")
                .setMessage("Apakah Anda yakin ingin mendaftar sebagai Mitra? Anda akan mendapatkan akses untuk membuat program donasi sendiri.")
                .setPositiveButton("Ya, Daftar", (dialog, which) -> daftarJadiMitra())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void daftarJadiMitra() {
        String userId = auth.getUid();
        if (userId == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("role", "mitra");

        db.collection("users").document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Selamat! Anda sekarang adalah Mitra.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal mendaftar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
