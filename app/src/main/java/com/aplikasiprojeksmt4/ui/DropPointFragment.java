package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.DropPointAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentDropPointBinding;

import android.net.Uri;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropPointFragment extends Fragment {

    private FragmentDropPointBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private DropPointAdapter adapter;
    private List<Map<String, Object>> dropPointList = new ArrayList<>();
    private Map<String, Object> selectedDropPoint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDropPointBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        loadDropPoints();

        binding.btnKonfirmasiLokasi.setOnClickListener(this::saveDonation);
    }

    private void setupRecyclerView() {
        adapter = new DropPointAdapter(dropPointList, item -> selectedDropPoint = item);
        binding.rvDropPoints.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDropPoints.setAdapter(adapter);
    }

    private void loadDropPoints() {
        db.collection("titik_penyaluran").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                dropPointList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Map<String, Object> data = doc.getData();
                    data.put("id", doc.getId());
                    dropPointList.add(data);
                }
                if (!dropPointList.isEmpty()) selectedDropPoint = dropPointList.get(0);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void saveDonation(View v) {
        if (getArguments() == null) return;

        String programId = getArguments().getString("programId");
        String programName = getArguments().getString("programName");
        String kondisi = getArguments().getString("kondisi");
        String deskripsi = getArguments().getString("deskripsi");
        String imageUriStr = getArguments().getString("imageUri");

        if (selectedDropPoint == null) {
            Toast.makeText(getContext(), "Pilih drop point terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnKonfirmasiLokasi.setEnabled(false);
        binding.btnKonfirmasiLokasi.setText("Memproses...");

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
                    binding.btnKonfirmasiLokasi.setEnabled(true);
                    binding.btnKonfirmasiLokasi.setText("Konfirmasi Lokasi");
                    Toast.makeText(getContext(), "Gagal unggah foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void performSave(View v, String programId, String programName, String kondisi, String deskripsi, String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        String dropPointName = (String) selectedDropPoint.get("nama");
        String dropPointAddr = (String) selectedDropPoint.get("alamat");

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
            donasi.put("metodePengiriman", "Drop Point: " + dropPointName);
            donasi.put("alamatPenjemputan", dropPointAddr);
            donasi.put("status", "Menunggu Verifikasi");
            donasi.put("timestamp", FieldValue.serverTimestamp());
            donasi.put("tanggalDonasi", new java.text.SimpleDateFormat("d MMM yyyy, HH.mm", new java.util.Locale("id", "ID")).format(new java.util.Date()));

            db.collection("donatur_barang").add(donasi)
                    .addOnSuccessListener(documentReference -> {
                        updateProgramStats(programId);
                        Bundle bundle = new Bundle();
                        bundle.putString("donationId", documentReference.getId());
                        Navigation.findNavController(v).navigate(R.id.action_DropPointFragment_to_KonfirmasiDonasiBarangFragment, bundle);
                    })
                    .addOnFailureListener(e -> {
                        binding.btnKonfirmasiLokasi.setEnabled(true);
                        binding.btnKonfirmasiLokasi.setText("Konfirmasi Lokasi");
                        Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateProgramStats(String programId) {
        if (programId == null) return;
        db.collection("programs").document(programId)
                .update("donatur_count", FieldValue.increment(1));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
