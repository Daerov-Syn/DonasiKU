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
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentDetailProgramDonasiuangBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailProgramDonasiUangFragment extends Fragment {

    private FragmentDetailProgramDonasiuangBinding binding;
    private FirebaseFirestore db;
    private String programId;
    private String programName;
    private String orgName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailProgramDonasiuangBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            programId = getArguments().getString("programId");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        if (programId != null) {
            loadProgramDetails();
        } else {
            Toast.makeText(getContext(), "ID Program tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        binding.btnDonateNow.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("programId", programId);
            bundle.putString("programName", programName);
            bundle.putString("orgName", orgName);
            Navigation.findNavController(v).navigate(R.id.action_DetailProgramDonasiUangFragment_to_DonasiDanaFragment, bundle);
        });
        
        binding.btnShare.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur berbagi akan segera hadir", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProgramDetails() {
        db.collection("programs").document(programId).get().addOnSuccessListener(documentSnapshot -> {
            Program p = documentSnapshot.toObject(Program.class);
            if (p != null) {
                p.setId(documentSnapshot.getId());
                displayData(p);
            }
        }).addOnFailureListener(e -> {
            Log.e("DetailProgram", "Gagal memuat data", e);
            Toast.makeText(getContext(), "Gagal memuat detail program", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayData(Program p) {
        this.programName = p.getNama();
        binding.tvProgramTitle.setText(p.getNama());
        
        this.orgName = p.getOrganisasi();
        if (orgName == null || orgName.isEmpty()) {
            orgName = p.getNama_pic();
        }
        if (orgName == null || orgName.isEmpty()) {
            orgName = "Pengelola Program";
        }
        binding.tvOrgName.setText(orgName);
        
        String initial = orgName.length() >= 3 ? orgName.substring(0, 3) : orgName;
        binding.tvOrgInitial.setText(initial.toUpperCase());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        
        long terkumpul = p.getTerkumpul();
        long target = p.getTargetValue();
        
        binding.tvCurrentFunds.setText(formatter.format(terkumpul));
        binding.tvTargetFunds.setText("Target: " + formatter.format(target));
        
        if (target > 0) {
            int progress = (int) ((terkumpul * 100) / target);
            binding.progressFunds.setProgress(progress);
            binding.tvProgressPercent.setText(progress + "% tercapai");
        } else {
            binding.progressFunds.setProgress(0);
            binding.tvProgressPercent.setText("0% tercapai");
        }

        binding.tvDonorsCount.setText("0");

        String bene = p.getPenerima_manfaat();
        if (bene != null) {
            String numeric = bene.replaceAll("[^0-9]", "");
            if (!numeric.isEmpty()) {
                binding.tvBeneficiaryCount.setText(numeric);
            } else {
                binding.tvBeneficiaryCount.setText("-");
            }
        }

        String batasWaktu = p.getBatas_waktu();
        if (batasWaktu != null && !batasWaktu.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("d / M / yyyy", Locale.getDefault());
                Date expiryDate = sdf.parse(batasWaktu);
                if (expiryDate != null) {
                    long diff = expiryDate.getTime() - System.currentTimeMillis();
                    long days = diff / (24 * 60 * 60 * 1000);
                    binding.tvDaysLeft.setText(String.valueOf(Math.max(0, days)));
                }
            } catch (Exception e) {
                binding.tvDaysLeft.setText("-");
            }
        }

        StringBuilder fullDesc = new StringBuilder();
        fullDesc.append(p.getDeskripsi());
        if (p.getPenerima_manfaat() != null && !p.getPenerima_manfaat().isEmpty()) {
            fullDesc.append("\n\nTarget Penerima:\n").append(p.getPenerima_manfaat());
        }
        if (p.getRencana_penggunaan() != null && !p.getRencana_penggunaan().isEmpty()) {
            fullDesc.append("\n\nRencana Penggunaan:\n").append(p.getRencana_penggunaan());
        }
        binding.tvProgramDescription.setText(fullDesc.toString());

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(p.getImageUrl())
                    .placeholder(R.drawable.group_2)
                    .error(R.drawable.group_2)
                    .into(binding.ivProgramDetail);
        } else {
            binding.ivProgramDetail.setImageResource(R.drawable.group_2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
