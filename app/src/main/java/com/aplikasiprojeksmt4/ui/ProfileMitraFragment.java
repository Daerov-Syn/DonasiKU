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

public class ProfileMitraFragment extends Fragment {

    private FragmentProfileMitraBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

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
        setupClickListeners();
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
            Toast.makeText(getContext(), "Rekening Bank", Toast.LENGTH_SHORT).show()
        );

        binding.btnNotif.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Notifikasi", Toast.LENGTH_SHORT).show()
        );

        binding.btnLegal.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Dokumen Legal", Toast.LENGTH_SHORT).show()
        );

        binding.btnHelp.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Bantuan", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
