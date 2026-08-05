package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentAlamatPenjemputanBinding;
import com.google.android.material.button.MaterialButton;

import android.net.Uri;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AlamatPenjemputanFragment extends Fragment {

    private FragmentAlamatPenjemputanBinding binding;
    private String selectedEkspedisi = "JNE";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlamatPenjemputanBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Default selection
        selectEkspedisi(binding.btnJne, "JNE");

        binding.btnJne.setOnClickListener(v -> selectEkspedisi(binding.btnJne, "JNE"));
        binding.btnJnt.setOnClickListener(v -> selectEkspedisi(binding.btnJnt, "J&T"));
        binding.btnSicepat.setOnClickListener(v -> selectEkspedisi(binding.btnSicepat, "SiCepat"));

        binding.btnKonfirmasiPenjemputan.setOnClickListener(v -> {
            saveDonation(v);
        });
    }

    private void saveDonation(View v) {
        if (getArguments() == null) return;

        String programId = getArguments().getString("programId");
        String programName = getArguments().getString("programName");
        String kondisi = getArguments().getString("kondisi");
        String deskripsi = getArguments().getString("deskripsi");
        String imageUriStr = getArguments().getString("imageUri");

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnKonfirmasiPenjemputan.setEnabled(false);
        binding.btnKonfirmasiPenjemputan.setText("Memproses...");

        if (imageUriStr != null) {
            uploadImageAndSave(v, programId, programName, kondisi, deskripsi, Uri.parse(imageUriStr));
        } else {
            performSave(v, programId, programName, kondisi, deskripsi, null);
        }
    }

    private void uploadImageAndSave(View v, String programId, String programName, String kondisi, String deskripsi, Uri imageUri) {
        String fileName = "donasi_" + java.util.UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("donasi_barang/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    performSave(v, programId, programName, kondisi, deskripsi, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    binding.btnKonfirmasiPenjemputan.setEnabled(true);
                    binding.btnKonfirmasiPenjemputan.setText("Konfirmasi Penjemputan");
                    Toast.makeText(getContext(), "Gagal unggah foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void performSave(View v, String programId, String programName, String kondisi, String deskripsi, String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            String namaDonatur = userDoc.getString("nama");
            if (namaDonatur == null) namaDonatur = "Anonim";

            Map<String, Object> donasi = new HashMap<>();
            donasi.put("programId", programId);
            donasi.put("programNama", programName);
            donasi.put("userId", userId);
            donasi.put("namaDonatur", namaDonatur);
            donasi.put("kondisi", kondisi);
            donasi.put("deskripsi", deskripsi);
            donasi.put("fotoBarang", imageUrl);
            donasi.put("metodePengiriman", "Ekspedisi " + selectedEkspedisi);
            donasi.put("alamatPenjemputan", binding.etAlamatLengkap.getText().toString());
            donasi.put("status", "Menunggu Verifikasi");
            donasi.put("timestamp", FieldValue.serverTimestamp());
            donasi.put("tanggalDonasi", new java.text.SimpleDateFormat("d MMM yyyy, HH.mm", new java.util.Locale("id", "ID")).format(new java.util.Date()));

            db.collection("donatur_barang").add(donasi)
                    .addOnSuccessListener(documentReference -> {
                        updateProgramStats(programId);
                        Bundle bundle = new Bundle();
                        bundle.putString("donationId", documentReference.getId());
                        Navigation.findNavController(v).navigate(R.id.action_AlamatPenjemputanFragment_to_KonfirmasiDonasiBarangFragment, bundle);
                    })
                    .addOnFailureListener(e -> {
                        binding.btnKonfirmasiPenjemputan.setEnabled(true);
                        binding.btnKonfirmasiPenjemputan.setText("Konfirmasi Penjemputan");
                        Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateProgramStats(String programId) {
        if (programId == null) return;
        // Sekarang hanya menambah donatur_count. 
        // 'terkumpul' akan ditambah setelah di-ACC oleh admin di DetailVerifikasiBarangFragment.
        db.collection("programs").document(programId)
                .update("donatur_count", FieldValue.increment(1));
    }

    private void selectEkspedisi(MaterialButton button, String ekspedisi) {
        selectedEkspedisi = ekspedisi;

        // Reset all buttons to outlined style
        resetButtonStyle(binding.btnJne);
        resetButtonStyle(binding.btnJnt);
        resetButtonStyle(binding.btnSicepat);

        // Set selected button to filled style
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        
        // Update shipping cost based on selection (mock data)
        if (ekspedisi.equals("JNE")) {
            binding.etOngkosKirim.setText("Rp 8.000");
        } else if (ekspedisi.equals("J&T")) {
            binding.etOngkosKirim.setText("Rp 10.000");
        } else {
            binding.etOngkosKirim.setText("Rp 9.000");
        }
    }

    private void resetButtonStyle(MaterialButton button) {
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        button.setStrokeColorResource(R.color.light_gray);
        button.setStrokeWidth(1);
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
