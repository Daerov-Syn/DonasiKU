package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.DonaturAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentDetailDonasiBarangBinding;
import com.aplikasiprojeksmt4.models.DonaturBarang;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class DetailDonasiBarangFragment extends Fragment {

    private FragmentDetailDonasiBarangBinding binding;
    private FirebaseFirestore db;
    private String programId;
    private List<DonaturBarang> donaturList = new ArrayList<>();
    private DonaturAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailDonasiBarangBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            programId = getArguments().getString("programId");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnDonasiSekarang.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("programId", programId);
            Navigation.findNavController(v).navigate(R.id.action_DetailDonasiBarangFragment_to_FormDonasiBarangFragment, bundle);
        });

        setupRecyclerView();
        loadProgramDetail();
        loadDonaturTerbaru();
    }

    private void setupRecyclerView() {
        adapter = new DonaturAdapter(donaturList);
        binding.rvDonaturTerbaru.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonaturTerbaru.setAdapter(adapter);
    }

    private void loadProgramDetail() {
        if (programId == null) return;

        db.collection("programs").document(programId)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    Program p = doc.toObject(Program.class);
                    if (p != null) {
                        binding.tvDetailNama.setText(p.getNama());
                        binding.tvDetailOrganisasi.setText(p.getOrganisasi());
                        binding.tvDetailDeskripsi.setText(p.getDeskripsi());
                        
                        // Inisial Organisasi
                        if (p.getOrganisasi() != null && !p.getOrganisasi().isEmpty()) {
                            String inisial = p.getOrganisasi().substring(0, Math.min(2, p.getOrganisasi().length())).toUpperCase();
                            binding.tvInisial.setText(inisial);
                        }

                        // Progress
                        long target = p.getTargetValue();
                        long terkumpul = p.getTerkumpul();
                        String unit = p.getTargetUnit();
                        
                        binding.tvDetailTerkumpul.setText(terkumpul + " " + unit);
                        binding.tvDetailTarget.setText("Target " + target + " " + unit);
                        
                        int progress = 0;
                        if (target > 0) {
                            progress = (int) ((terkumpul * 100) / target);
                        }
                        binding.progressDetail.setProgress(progress);
                        binding.tvDetailPersen.setText(progress + "% tercapai");

                        // Image
                        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                            Glide.with(this).load(p.getImageUrl()).into(binding.ivProgramDetail);
                        }
                        
                        // Placeholder data for stats (Can be linked to other collections later)
                        binding.tvPenerimaCount.setText("45");
                        binding.tvHariLagi.setText("12");
                    }
                });
    }

    private void loadDonaturTerbaru() {
        if (programId == null) return;

        db.collection("donatur_barang")
                .whereEqualTo("programId", programId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        donaturList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            DonaturBarang d = doc.toObject(DonaturBarang.class);
                            if (d != null) {
                                d.setId(doc.getId());
                                donaturList.add(d);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.tvDonaturCount.setText(String.valueOf(value.size()));
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
