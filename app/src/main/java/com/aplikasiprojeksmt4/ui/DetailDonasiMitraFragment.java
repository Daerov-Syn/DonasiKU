package com.aplikasiprojeksmt4.ui;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.adapters.DonaturDanaAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentDetailDonasiMitraBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailDonasiMitraFragment extends Fragment {

    private FragmentDetailDonasiMitraBinding binding;
    private Program program;
    private FirebaseFirestore db;
    private DonaturDanaAdapter adapter;
    private List<DonaturDana> donaturList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            program = (Program) getArguments().getSerializable("program");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailDonasiMitraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (program != null) {
            displayProgramDetails();
            setupDonaturRecyclerView();
            loadDonaturList();
        }

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnHentikanProgram.setOnClickListener(v -> {
            if (program != null) {
                showHentikanConfirmation();
            }
        });
    }

    private void setupDonaturRecyclerView() {
        adapter = new DonaturDanaAdapter(donaturList);
        binding.rvDonaturProgram.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonaturProgram.setAdapter(adapter);
    }

    private void loadDonaturList() {
        if (program.getId() == null) return;

        db.collection("donatur_dana")
                .whereEqualTo("programId", program.getId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("DetailDonasiMitra", "Gagal memuat donatur: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        donaturList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            DonaturDana d = doc.toObject(DonaturDana.class);
                            donaturList.add(d);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void displayProgramDetails() {
        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivProgramDetail);
        }

        binding.tvTagTipe.setText(program.getTipe());
        binding.tvTagStatus.setText(program.getStatus());
        binding.tvDetailNama.setText(program.getNama());
        binding.tvWilayahDetail.setText(program.getWilayah());

        long terkumpul = program.getTerkumpul();
        long target = program.getTargetValue();
        String targetUnit = program.getTargetUnit();

        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            binding.tvAmountTarget.setText("Rp " + formatNumber(terkumpul) + " / " + formatNumber(target));
        } else {
            binding.tvAmountTarget.setText(terkumpul + " / " + target + " " + targetUnit);
        }

        binding.tvDonaturCount.setText(String.valueOf(program.getDonatur_count()));
        binding.tvPenerimaCount.setText(String.valueOf(program.getPenerima_count()));

        int progress = 0;
        if (target > 0) {
            progress = (int) ((terkumpul * 100) / target);
        }
        binding.progressDetail.setProgress(progress);
        binding.tvProgressPercent.setText(progress + "%");

        if ("Selesai".equalsIgnoreCase(program.getStatus())) {
            binding.btnHentikanProgram.setVisibility(View.GONE);
        } else {
            binding.btnHentikanProgram.setVisibility(View.VISIBLE);
        }
    }

    private String formatNumber(long number) {
        return String.format("%,d", number).replace(',', '.');
    }

    private void showHentikanConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Selesaikan Program")
                .setMessage("Apakah Anda yakin ingin memberhentikan/menyelesaikan program ini?")
                .setPositiveButton("Ya, Selesaikan", (dialog, which) -> finalizeProgram())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void finalizeProgram() {
        if (program.getId() == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Selesai");

        db.collection("programs").document(program.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Program telah diselesaikan", Toast.LENGTH_SHORT).show();
                    program.setStatus("Selesai");
                    displayProgramDetails();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
