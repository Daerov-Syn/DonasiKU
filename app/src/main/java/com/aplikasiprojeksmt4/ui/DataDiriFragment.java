package com.aplikasiprojeksmt4.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentDataDiriBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DataDiriFragment extends Fragment {

    private FragmentDataDiriBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (binding != null) {
                        binding.ivProfilePicture.setImageURI(imageUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDataDiriBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://test-rizha.firebasestorage.app");
        sessionManager = new SessionManager(requireContext());
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.tvGantiFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnSimpan.setOnClickListener(v -> saveChanges());
    }

    private void checkEmailVerificationStatus(boolean isVerifiedInDb) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Reload user data from Firebase Auth to get the latest emailVerified status
            user.reload().addOnCompleteListener(task -> {
                if (binding == null || !isAdded()) return;

                boolean finalVerifiedStatus = isVerifiedInDb;
                if (task.isSuccessful()) {
                    // Sync: if verified in Auth but not in DB, update DB
                    if (user.isEmailVerified() && !isVerifiedInDb) {
                        finalVerifiedStatus = true;
                        updateEmailVerifiedInDb(true);
                    }
                }
                updateUIForVerification(finalVerifiedStatus);
            });
        } else {
            // Fallback to DB status if Auth user is not available
            updateUIForVerification(isVerifiedInDb);
        }
    }

    private void updateUIForVerification(boolean isVerified) {
        if (binding == null) return;

        binding.btnVerifyEmail.setVisibility(View.VISIBLE);

        if (isVerified) {
            // Already verified: view-only mode
            binding.etEmail.setEnabled(false);
            binding.btnVerifyEmail.setText("Email Terverifikasi");
            binding.btnVerifyEmail.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            binding.btnVerifyEmail.setEnabled(false);
            try {
                binding.btnVerifyEmail.setIcon(getResources().getDrawable(android.R.drawable.checkbox_on_background));
                binding.btnVerifyEmail.setIconTintResource(android.R.color.holo_green_dark);
            } catch (Exception ignored) {}
        } else {
            // Not verified: editable + show verification button
            binding.etEmail.setEnabled(true);
            binding.btnVerifyEmail.setText("Verifikasi Sekarang");
            binding.btnVerifyEmail.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            binding.btnVerifyEmail.setOnClickListener(v -> sendVerificationEmail());
        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            binding.btnVerifyEmail.setEnabled(false);
            binding.btnVerifyEmail.setText("Mengirim...");
            
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (binding == null) return;
                        binding.btnVerifyEmail.setEnabled(true);
                        binding.btnVerifyEmail.setText("Verifikasi Sekarang");
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Link verifikasi telah dikirim ke " + user.getEmail() + ". Silakan cek kotak masuk atau folder spam Anda.", Toast.LENGTH_LONG).show();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Terjadi kesalahan";
                            Toast.makeText(getContext(), "Gagal mengirim link: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Sesi login tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmailVerifiedInDb(boolean status) {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            db.collection("users").document(userId).update("emailVerified", status);
        }
    }

    private void loadUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (binding == null || !isAdded()) return;
                    
                    if (documentSnapshot.exists()) {
                        String nama = documentSnapshot.getString("nama");
                        String telepon = documentSnapshot.getString("no_telepon");
                        String alamat = documentSnapshot.getString("alamat");
                        String photoUrl = documentSnapshot.getString("profile_photo");
                        String email = documentSnapshot.getString("email");
                        Boolean emailVerified = documentSnapshot.getBoolean("emailVerified");
                        
                        // Ensure 'emailVerified' column exists in Firestore
                        if (emailVerified == null) {
                            emailVerified = false;
                            updateEmailVerifiedInDb(false);
                        }

                        binding.etNama.setText(nama != null ? nama : "");
                        binding.etTelepon.setText(telepon != null ? telepon : "");
                        binding.etAlamat.setText(alamat != null ? alamat : "");
                        
                        if (email != null) {
                            binding.etEmail.setText(email);
                        } else if (mAuth.getCurrentUser() != null) {
                            binding.etEmail.setText(mAuth.getCurrentUser().getEmail());
                        }

                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(this).load(photoUrl).into(binding.ivProfilePicture);
                        }
                        
                        checkEmailVerificationStatus(emailVerified);
                    } else {
                        // Create initial 'emailVerified' column if user doc doesn't exist
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            binding.etEmail.setText(user.getEmail());
                            checkEmailVerificationStatus(user.isEmailVerified());
                        }
                    }
                });
    }

    private void saveChanges() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        binding.btnSimpan.setEnabled(false);
        binding.btnSimpan.setText("Menyimpan...");

        if (imageUri != null) {
            uploadImageAndSaveData(userId);
        } else {
            saveDataToFirestore(userId, null);
        }
    }

    private void uploadImageAndSaveData(String userId) {
        StorageReference ref = storage.getReference().child("profile_photos/" + userId + ".jpg");
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (binding != null) {
                        saveDataToFirestore(userId, uri.toString());
                    }
                }))
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        binding.btnSimpan.setEnabled(true);
                        binding.btnSimpan.setText("Simpan Perubahan");
                        Toast.makeText(getContext(), "Gagal upload foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDataToFirestore(String userId, @Nullable String photoUrl) {
        if (binding == null) return;

        String nama = binding.etNama.getText().toString().trim();
        String telepon = binding.etTelepon.getText().toString().trim();
        String alamat = binding.etAlamat.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", nama);
        updates.put("no_telepon", telepon);
        updates.put("alamat", alamat);
        updates.put("email", email);
        // Do not update emailVerified here, let it be handled by sync logic
        
        if (photoUrl != null) {
            updates.put("profile_photo", photoUrl);
        }

        db.collection("users").document(userId).set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (binding != null) {
                        sessionManager.saveUsername(nama);
                        Toast.makeText(getContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(binding.getRoot()).navigateUp();
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        binding.btnSimpan.setEnabled(true);
                        binding.btnSimpan.setText("Simpan Perubahan");
                        Toast.makeText(getContext(), "Gagal memperbarui data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
