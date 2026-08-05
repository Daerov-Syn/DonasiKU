package com.aplikasiprojeksmt4.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.ProgramDonaturAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentDetailDonasiMitraBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailDonasiMitraFragment extends Fragment {

    private FragmentDetailDonasiMitraBinding binding;
    private Program program;
    private FirebaseFirestore db;
    private ProgramDonaturAdapter adapter;
    private List<DonaturDana> donaturList = new ArrayList<>();
    private ListenerRegistration programListener;

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
            listenToProgramUpdates();
            loadDonaturList();
        }

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupDonaturRecyclerView();
        setupTabs();

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

    private void listenToProgramUpdates() {
        programListener = db.collection("programs").document(program.getId())
                .addSnapshotListener((doc, error) -> {
                    if (binding == null || !isAdded()) return;
                    if (doc != null && doc.exists()) {
                        program = doc.toObject(Program.class);
                        if (program != null) {
                            program.setId(doc.getId());
                            displayProgramDetails();
                        }
                    }
                });
    }

    private void setupTabs() {
        binding.tvTabInfo.setOnClickListener(v -> switchTab("info"));
        binding.tvTabDonatur.setOnClickListener(v -> switchTab("donatur"));
        binding.tvTabLaporan.setOnClickListener(v -> switchTab("laporan"));
        
        switchTab("info"); // Default
    }

    private void switchTab(String tab) {
        // Reset Styles
        binding.tvTabInfo.setTextColor(Color.parseColor("#888888"));
        binding.tvTabInfo.setTypeface(null, Typeface.NORMAL);
        binding.tvTabDonatur.setTextColor(Color.parseColor("#888888"));
        binding.tvTabDonatur.setTypeface(null, Typeface.NORMAL);
        binding.tvTabLaporan.setTextColor(Color.parseColor("#888888"));
        binding.tvTabLaporan.setTypeface(null, Typeface.NORMAL);

        binding.layoutInfoContent.setVisibility(View.GONE);
        binding.layoutDonaturContent.setVisibility(View.GONE);
        binding.layoutLaporanContent.setVisibility(View.GONE);

        int activeColor = ContextCompat.getColor(requireContext(), R.color.primary_purple);

        if (tab.equals("info")) {
            binding.tvTabInfo.setTextColor(activeColor);
            binding.tvTabInfo.setTypeface(null, Typeface.BOLD);
            binding.layoutInfoContent.setVisibility(View.VISIBLE);
        } else if (tab.equals("donatur")) {
            binding.tvTabDonatur.setTextColor(activeColor);
            binding.tvTabDonatur.setTypeface(null, Typeface.BOLD);
            binding.layoutDonaturContent.setVisibility(View.VISIBLE);
        } else {
            binding.tvTabLaporan.setTextColor(activeColor);
            binding.tvTabLaporan.setTypeface(null, Typeface.BOLD);
            binding.layoutLaporanContent.setVisibility(View.VISIBLE);
        }
    }

    private void setupDonaturRecyclerView() {
        adapter = new ProgramDonaturAdapter(donaturList);
        binding.rvDonaturTab.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonaturTab.setAdapter(adapter);
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
        binding.tvOrgName.setText(program.getOrganisasi() != null ? program.getOrganisasi() : program.getDibuat_oleh_nama());
        
        String org = binding.tvOrgName.getText().toString();
        if (!org.isEmpty()) {
            binding.tvOrgInitial.setText(org.substring(0, Math.min(3, org.length())).toUpperCase());
        }

        binding.tvDeskripsiTab.setText(program.getDeskripsi());

        // Tarik Dana Visibility logic
        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            binding.btnTarikDana.setVisibility(View.VISIBLE);
        } else {
            binding.btnTarikDana.setVisibility(View.GONE);
        }

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);

        long target = program.getTargetValue();
        long terkumpul = program.getTerkumpul();
        String unit = program.getTargetUnit();

        binding.tvCurrentAmount.setText(nf.format(terkumpul));
        binding.tvTargetAmount.setText(getString(R.string.target_label, nf.format(target)));
        
        if (!"Dana".equalsIgnoreCase(program.getTipe())) {
            binding.tvCurrentAmount.setText(getString(R.string.unit_format, terkumpul, unit));
            binding.tvTargetAmount.setText(getString(R.string.target_label, terkumpul + " " + unit)); // Need proper string res
        }

        binding.tvDonaturCount.setText(String.valueOf(program.getDonatur_count()));
        binding.tvPenerimaCount.setText(String.valueOf(program.getPenerima_count()));
        binding.tvHariLagi.setText(calculateDaysLeft(program.getBatas_waktu()));
        binding.tvSiapTarik.setText(nf.format(program.getSiap_tarik()));

        int progress = 0;
        if (target > 0) {
            progress = (int) ((terkumpul * 100) / target);
        }
        binding.progressDetail.setProgress(progress);
        binding.tvDetailPersen.setText(getString(R.string.percent_tercapai, progress));

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivProgramDetail);
        }

        // Report Tab Summary
        binding.tvRepTotal.setText(nf.format(terkumpul));
        binding.tvRepDitarik.setText(nf.format(program.getSudah_ditarik()));
        binding.tvRepSiap.setText(nf.format(program.getSiap_tarik()));
    }

    private String calculateDaysLeft(String deadline) {
        if (deadline == null || deadline.isEmpty()) return "0";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(deadline);
            if (date == null) return "0";
            long diff = date.getTime() - System.currentTimeMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            return String.valueOf(Math.max(0, days));
        } catch (Exception e) {
            return "0";
        }
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
        if (programListener != null) programListener.remove();
        binding = null;
    }
}
