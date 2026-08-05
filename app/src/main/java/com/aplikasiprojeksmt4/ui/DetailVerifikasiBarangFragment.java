package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.databinding.PageDetailVerifikasiBarangBinding;
import com.aplikasiprojeksmt4.models.DonaturBarang;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailVerifikasiBarangFragment extends Fragment {

    private PageDetailVerifikasiBarangBinding binding;
    private FirebaseFirestore db;
    private String donationId;
    private String programId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageDetailVerifikasiBarangBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            donationId = getArguments().getString("donationId");
            programId = getArguments().getString("programId");
        }

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        loadDonationDetail();

        binding.btnSetujuDetail.setOnClickListener(v -> verifyDonation("Diverifikasi"));
        binding.btnTolakDetail.setOnClickListener(v -> verifyDonation("Ditolak"));
    }

    private void loadDonationDetail() {
        if (donationId == null) return;

        db.collection("donatur_barang").document(donationId)
                .get()
                .addOnSuccessListener(doc -> {
                    DonaturBarang d = doc.toObject(DonaturBarang.class);
                    if (d != null) {
                        binding.tvNamaDonatur.setText(d.getNamaDonatur());
                        binding.tvDeskripsiBarang.setText(d.getDeskripsi());
                        binding.tvKondisiBarang.setText(d.getKondisi());
                        
                        if (d.getNamaDonatur() != null && !d.getNamaDonatur().isEmpty()) {
                            binding.tvInisial.setText(d.getNamaDonatur().substring(0, 1).toUpperCase());
                        }

                        if (d.getTimestamp() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                            binding.tvWaktuUpload.setText("Diunggah " + sdf.format(d.getTimestamp()));
                        }

                        if (d.getFotoBarang() != null && !d.getFotoBarang().isEmpty()) {
                            Glide.with(this).load(d.getFotoBarang()).into(binding.ivFotoBarang);
                        }
                        
                        programId = d.getProgramId();
                    }
                });
    }

    private void verifyDonation(String status) {
        if (donationId == null) return;

        String catatan = binding.etCatatanAdmin.getText().toString();

        db.collection("donatur_barang").document(donationId)
                .update("status", status, "catatanAdmin", catatan)
                .addOnSuccessListener(aVoid -> {
                    if (status.equals("Diverifikasi") && programId != null) {
                        // Jika disetujui, baru tambahkan ke progress program
                        db.collection("programs").document(programId)
                                .update("terkumpul", FieldValue.increment(1));
                    }
                    Toast.makeText(getContext(), "Donasi Berhasil " + status, Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
