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

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentProfileMitraBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProfileMitraFragment extends Fragment {

    private FragmentProfileMitraBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListenerRegistration statsListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileMitraBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserProfile();
        loadMitraStats();
        setupClickListeners();
    }

    private void loadMitraStats() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        statsListener = db.collection("programs")
                .whereEqualTo("dibuat_oleh", currentUser.getUid())
                .addSnapshotListener((value, error) -> {
                    if (isAdded() && value != null) {
                        int totalProgram = value.size();
                        long totalDonatur = 0;
                        long totalPenerima = 0;

                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            Long donatur = doc.getLong("donatur_count");
                            Long penerima = doc.getLong("penerima_count");
                            if (donatur != null) totalDonatur += donatur;
                            if (penerima != null) totalPenerima += penerima;
                        }

                        binding.tvStatProgram.setText(String.valueOf(totalProgram));
                        binding.tvStatDonatur.setText(String.valueOf(totalDonatur));
                        binding.tvStatPenerima.setText(String.valueOf(totalPenerima));
                    }
                });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        // Ambil data dari koleksi 'users' sesuai permintaan
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String email = documentSnapshot.getString("email");
                        String fotoUrl = documentSnapshot.getString("fotoUrl");
                        if (fotoUrl == null) fotoUrl = documentSnapshot.getString("foto");

                        if (name != null && !name.isEmpty()) {
                            binding.tvProfileName.setText(name);
                        }

                        if (email != null && !email.isEmpty()) {
                            binding.tvProfileEmail.setText(email);
                        } else {
                            binding.tvProfileEmail.setText(currentUser.getEmail());
                        }

                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            updateProfileImage(fotoUrl);
                        } else if (currentUser.getPhotoUrl() != null) {
                            updateProfileImage(currentUser.getPhotoUrl().toString());
                        } else {
                            binding.ivProfileAvatar.setImageResource(R.drawable.logo);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileMitra", "Gagal load data users", e));
    }

    private void updateProfileImage(String url) {
        if (!isAdded()) return;
        Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(binding.ivProfileAvatar);
    }

    private void setupClickListeners() {
        binding.btnEditProfile.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Edit Profil", Toast.LENGTH_SHORT).show()
        );

        binding.btnRekening.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_Profile_to_RekeningBank)
        );

        binding.btnNotif.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_Profile_to_PengaturanNotifikasi)
        );

        binding.btnLegal.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_Profile_to_DokumenLegal)
        );

        binding.btnHelp.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.action_Profile_to_BantuanFaq)
        );

        binding.btnLogout.setOnClickListener(v -> {
            // 1. Keluar dari sesi Firebase Auth
            FirebaseAuth.getInstance().signOut();

            // 2. Pindah ke halaman Login dan bersihkan tumpukan halaman
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.action_ProfileMitra_to_Login);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (statsListener != null) {
            statsListener.remove();
        }
        binding = null;
    }
}
