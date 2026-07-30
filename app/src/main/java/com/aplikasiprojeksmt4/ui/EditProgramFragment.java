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

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentEditProgramBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProgramFragment extends Fragment {

    private FragmentEditProgramBinding binding;
    private Program program;
    private FirebaseFirestore db;

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
        binding = FragmentEditProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (program != null) {
            populateData();
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnSimpanPerubahan.setOnClickListener(v -> saveChanges());
        
        binding.btnGantiFoto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Ganti Foto", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateData() {
        binding.etJudulProgram.setText(program.getNama());
        binding.etTargetDana.setText(String.valueOf(program.getTargetValue()));
        binding.etPenerimaManfaat.setText("Keluarga pra-sejahtera"); // Placeholder if not in model
        binding.etDeskripsiProgram.setText(program.getDeskripsi());
        binding.tvHeaderSubTitle.setText(program.getDibuat_oleh_nama() != null ? program.getDibuat_oleh_nama() : "Mitra");

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivProgramBanner);
        }

        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            setKategoriDana();
        } else {
            setKategoriBarang();
        }
    }

    private void setKategoriDana() {
        binding.btnKategoriDana.setBackgroundTintList(getResources().getColorStateList(R.color.primary_purple));
        binding.btnKategoriDana.setTextColor(getResources().getColor(R.color.white));
        binding.btnKategoriBarang.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        binding.btnKategoriBarang.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setKategoriBarang() {
        binding.btnKategoriBarang.setBackgroundTintList(getResources().getColorStateList(R.color.primary_purple));
        binding.btnKategoriBarang.setTextColor(getResources().getColor(R.color.white));
        binding.btnKategoriDana.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        binding.btnKategoriDana.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void saveChanges() {
        String nama = binding.etJudulProgram.getText().toString().trim();
        String deskripsi = binding.etDeskripsiProgram.getText().toString().trim();
        String targetStr = binding.etTargetDana.getText().toString().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || targetStr.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi semua field wajib", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", nama);
        updates.put("deskripsi", deskripsi);
        updates.put("target", targetStr);

        db.collection("programs").document(program.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Program berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal memperbarui program", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
