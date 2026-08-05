package com.aplikasiprojeksmt4.ui;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.DonaturDanaAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentDetailDonasiMitraBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailDonasiMitraBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (program != null) {
            displayProgramDetails();
            loadDonaturList();
        }

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupDonaturRecyclerView();

        // Action Buttons
        binding.btnTarikDana.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(v).navigate(R.id.action_DetailDonasiMitraFragment_to_TarikDanaFragment, bundle);
        });

        binding.btnEditProgram.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("program", program);
            Navigation.findNavController(v).navigate(R.id.action_DetailDonasiMitraFragment_to_EditProgramFragment, bundle);
        });

        binding.btnHapusProgram.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupDonaturRecyclerView() {
        adapter = new DonaturDanaAdapter(donaturList);
        binding.rvDonaturProgram.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonaturProgram.setAdapter(adapter);
    }

    private void loadDonaturList() {
        db.collection("donatur_dana")
                .whereEqualTo("programId", program.getId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
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
        binding.tvDetailNama.setText(program.getNama());
        binding.tvWilayahDetail.setText(program.getWilayah());
        binding.tvTagTipe.setText(program.getTipe());
        binding.tvTagStatus.setText(program.getStatus());
        binding.tvDeskripsiDetail.setText(program.getDeskripsi());

        // Exception: Withdraw Funds only for Money donations
        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            binding.btnTarikDana.setVisibility(View.VISIBLE);
        } else {
            binding.btnTarikDana.setVisibility(View.GONE);
        }

        long target = program.getTargetValue();
        long terkumpul = program.getTerkumpul();
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

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivProgramDetail);
        }
    }

    private String formatNumber(long number) {
        return java.text.NumberFormat.getInstance().format(number);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Program")
                .setMessage("Apakah Anda yakin ingin menghapus program ini secara permanen?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteProgram())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteProgram() {
        db.collection("programs").document(program.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Program dihapus", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal menghapus", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
