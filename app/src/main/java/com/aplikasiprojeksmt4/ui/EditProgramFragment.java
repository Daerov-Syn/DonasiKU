package com.aplikasiprojeksmt4.ui;

import android.app.DatePickerDialog;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProgramFragment extends Fragment {

    private FragmentEditProgramBinding binding;
    private Program program;
    private FirebaseFirestore db;
    private String selectedTipe;

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
        binding.btnBatal.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnSimpanPerubahan.setOnClickListener(v -> saveChanges());
        
        binding.btnGantiFoto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Ganti Foto (Coming Soon)", Toast.LENGTH_SHORT).show();
        });

        binding.etBatasWaktu.setOnClickListener(v -> showDatePicker());
        
        // Category locked for existing programs
        binding.btnKategoriDana.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Kategori tidak dapat diubah", Toast.LENGTH_SHORT).show();
        });
        binding.btnKategoriBarang.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Kategori tidak dapat diubah", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateData() {
        binding.etJudulProgram.setText(program.getNama());
        binding.etTargetDana.setText(String.valueOf(program.getTargetValue()));
        binding.etBatasWaktu.setText(program.getBatas_waktu());
        binding.etPenerimaManfaat.setText(program.getPenerima_manfaat());
        binding.etDeskripsiProgram.setText(program.getDeskripsi());
        binding.etNamaPIC.setText(program.getNama_pic());
        binding.etNoWhatsApp.setText(program.getNo_whatsapp());
        
        binding.tvHeaderSubTitle.setText(program.getDibuat_oleh_nama() != null ? program.getDibuat_oleh_nama() : "Mitra");

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivProgramBanner);
        }

        if ("Dana".equalsIgnoreCase(program.getTipe())) {
            setKategoriDana();
            binding.tvLabelTarget.setText("Target Dana (Rp) *");
        } else {
            setKategoriBarang();
            binding.tvLabelTarget.setText("Target Barang (Unit) *");
        }
    }

    private void setKategoriDana() {
        selectedTipe = "Dana";
        binding.btnKategoriDana.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
        binding.btnKategoriDana.setTextColor(android.graphics.Color.WHITE);
        binding.btnKategoriBarang.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        binding.btnKategoriBarang.setTextColor(android.graphics.Color.GRAY);
    }

    private void setKategoriBarang() {
        selectedTipe = "Barang";
        binding.btnKategoriBarang.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
        binding.btnKategoriBarang.setTextColor(android.graphics.Color.WHITE);
        binding.btnKategoriDana.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        binding.btnKategoriDana.setTextColor(android.graphics.Color.GRAY);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
            binding.etBatasWaktu.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveChanges() {
        String nama = binding.etJudulProgram.getText().toString().trim();
        String deskripsi = binding.etDeskripsiProgram.getText().toString().trim();
        String targetStr = binding.etTargetDana.getText().toString().trim();
        String deadline = binding.etBatasWaktu.getText().toString().trim();
        String penerima = binding.etPenerimaManfaat.getText().toString().trim();
        String pic = binding.etNamaPIC.getText().toString().trim();
        String wa = binding.etNoWhatsApp.getText().toString().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || targetStr.isEmpty() || deadline.isEmpty() || pic.isEmpty() || wa.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi semua field bertanda *", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", nama);
        updates.put("deskripsi", deskripsi);
        updates.put("target", targetStr + ("Dana".equalsIgnoreCase(selectedTipe) ? "" : " Unit"));
        updates.put("tipe", selectedTipe);
        updates.put("batas_waktu", deadline);
        updates.put("penerima_manfaat", penerima);
        updates.put("nama_pic", pic);
        updates.put("no_whatsapp", wa);

        db.collection("programs").document(program.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Program berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal memperbarui program: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
