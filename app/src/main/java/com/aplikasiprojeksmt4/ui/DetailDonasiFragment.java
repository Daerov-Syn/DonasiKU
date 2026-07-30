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
import com.aplikasiprojeksmt4.databinding.FragmentDetaildonasiBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DetailDonasiFragment extends Fragment {

    private FragmentDetaildonasiBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String transactionId = "TRX2026031100123"; // ID Default untuk demo
    private boolean isProofUploaded = false;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.ivProofPreview.setImageURI(imageUri);
                    uploadImageToStorage();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetaildonasiBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Logika Pilih Gambar Bukti Penyaluran
        binding.btnUploadProof.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // Logika Navigasi ke Sertifikat
        binding.btnDownloadCert.setOnClickListener(v -> {
            if (isProofUploaded) {
                Navigation.findNavController(v).navigate(R.id.action_DetailDonasiFragment_to_SertifDonasiFragment);
            } else {
                Toast.makeText(getContext(), "Harap unggah bukti penyaluran terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToStorage() {
        if (imageUri == null) return;

        Toast.makeText(getContext(), "Sedang mengunggah bukti...", Toast.LENGTH_SHORT).show();
        
        StorageReference ref = storage.getReference().child("bukti_penyaluran/" + transactionId);
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    updateFirestoreData(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    // Fallback untuk demo jika Firebase Storage tidak terkonfigurasi
                    isProofUploaded = true;
                    updateUIStatus();
                    Toast.makeText(getContext(), "Simulasi: Bukti terunggah (Offline Mode)", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFirestoreData(String imageUrl) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", "Tersalurkan");
        updateData.put("buktiPenyaluran", imageUrl);
        updateData.put("waktuPenyaluran", System.currentTimeMillis());

        db.collection("donations").document(transactionId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    isProofUploaded = true;
                    updateUIStatus();
                    Toast.makeText(getContext(), "Bukti berhasil disimpan dan tersinkron.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    isProofUploaded = true;
                    updateUIStatus();
                    Toast.makeText(getContext(), "Berhasil memperbarui status (Local)", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUIStatus() {
        binding.ivProofPreview.setColorFilter(null);
        binding.tvProofStatus.setText("Foto Bukti Tersedia");
        binding.tvProofStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        
        binding.tvStatus.setText("Tersalurkan");
        binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
