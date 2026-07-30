package com.aplikasiprojeksmt4.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentTambahProgramBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahProgramFragment extends Fragment {

    private FragmentTambahProgramBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private SessionManager sessionManager;
    private Uri imageUri;
    private String selectedKategori = "Dana"; // Default

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.ivProgramPreview.setImageURI(imageUri);
                    binding.ivProgramPreview.setVisibility(View.VISIBLE);
                    binding.llUploadPlaceholder.setVisibility(View.GONE);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTambahProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(requireContext());
        storage = FirebaseStorage.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStepNavigation();
        setupKategoriSelection();
        setupChips();
        setupDatePicker();
        
        binding.btnBack.setOnClickListener(v -> handleBack());
        binding.cvUploadImage.setOnClickListener(v -> pickImage());
        binding.btnBuatProgramFinal.setOnClickListener(v -> saveProgram());
    }

    private void handleBack() {
        if (binding.layoutStep2.getVisibility() == View.VISIBLE) {
            goToStep(1);
        } else if (binding.layoutStep3.getVisibility() == View.VISIBLE) {
            goToStep(2);
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    private void setupStepNavigation() {
        binding.btnLanjut1.setOnClickListener(v -> {
            if (validateStep1()) {
                goToStep(2);
            }
        });

        binding.btnLanjut2.setOnClickListener(v -> {
            if (validateStep2()) {
                updateSummary();
                goToStep(3);
            }
        });
    }

    private void goToStep(int step) {
        binding.layoutStep1.setVisibility(View.GONE);
        binding.layoutStep2.setVisibility(View.GONE);
        binding.layoutStep3.setVisibility(View.GONE);
        binding.layoutSuccess.setVisibility(View.GONE);

        binding.progress1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_translucent));
        binding.progress2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_translucent));
        binding.progress3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_translucent));

        switch (step) {
            case 1:
                binding.layoutStep1.setVisibility(View.VISIBLE);
                binding.tvStepIndicator.setText("Langkah 1 dari 3 — Info Dasar");
                binding.progress1.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case 2:
                binding.layoutStep2.setVisibility(View.VISIBLE);
                binding.tvStepIndicator.setText("Langkah 2 dari 3 — Deskripsi");
                binding.progress1.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                binding.progress2.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case 3:
                binding.layoutStep3.setVisibility(View.VISIBLE);
                binding.tvStepIndicator.setText("Langkah 3 dari 3 — Kontak PIC");
                binding.progress1.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                binding.progress2.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                binding.progress3.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case 4:
                binding.layoutSuccess.setVisibility(View.VISIBLE);
                binding.llHeader.setVisibility(View.GONE);
                binding.scrollViewForm.setPadding(0,0,0,0);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.scrollViewForm.getLayoutParams();
                params.topMargin = 0;
                binding.scrollViewForm.setLayoutParams(params);
                break;
        }
        binding.scrollViewForm.scrollTo(0, 0);
    }

    private void setupKategoriSelection() {
        binding.btnKategoriDana.setOnClickListener(v -> {
            selectedKategori = "Dana";
            updateKategoriUI();
        });
        binding.btnKategoriBarang.setOnClickListener(v -> {
            selectedKategori = "Barang";
            updateKategoriUI();
        });
    }

    private void updateKategoriUI() {
        if (selectedKategori.equals("Dana")) {
            binding.btnKategoriDana.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
            binding.btnKategoriDana.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.btnKategoriBarang.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.light_gray));
            binding.btnKategoriBarang.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray));
            binding.tvLabelTarget.setText("Target Dana (Rp) *");
            binding.etTargetDana.setHint("10.000.000");
            binding.scrollChips.setVisibility(View.VISIBLE);
        } else {
            binding.btnKategoriBarang.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
            binding.btnKategoriBarang.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.btnKategoriDana.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.light_gray));
            binding.btnKategoriDana.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray));
            binding.btnKategoriDana.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
            binding.tvLabelTarget.setText("Target Barang *");
            binding.etTargetDana.setHint("Contoh: 100 Paket Sembako");
            binding.scrollChips.setVisibility(View.GONE);
        }
    }

    private void setupChips() {
        binding.chip5jt.setOnClickListener(v -> binding.etTargetDana.setText("5000000"));
        binding.chip10jt.setOnClickListener(v -> binding.etTargetDana.setText("10000000"));
        binding.chip20jt.setOnClickListener(v -> binding.etTargetDana.setText("20000000"));
        binding.chip30jt.setOnClickListener(v -> binding.etTargetDana.setText("30000000"));
    }

    private void setupDatePicker() {
        binding.etBatasWaktu.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> 
                            binding.etBatasWaktu.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year1),
                    year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private boolean validateStep1() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Pilih foto program", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(binding.etNamaProgram.getText())) {
            binding.etNamaProgram.setError("Judul program wajib diisi");
            return false;
        }
        if (TextUtils.isEmpty(binding.etTargetDana.getText())) {
            binding.etTargetDana.setError("Target wajib diisi");
            return false;
        }
        if (TextUtils.isEmpty(binding.etBatasWaktu.getText())) {
            binding.etBatasWaktu.setError("Batas waktu wajib diisi");
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        if (TextUtils.isEmpty(binding.etDeskripsiProgram.getText())) {
            binding.etDeskripsiProgram.setError("Deskripsi wajib diisi");
            return false;
        }
        if (TextUtils.isEmpty(binding.etPenerimaManfaat.getText())) {
            binding.etPenerimaManfaat.setError("Penerima manfaat wajib diisi");
            return false;
        }
        return true;
    }

    private void updateSummary() {
        binding.tvSumJudul.setText(binding.etNamaProgram.getText().toString());
        binding.tvSumKategori.setText(selectedKategori);
        binding.tvSumTarget.setText(binding.etTargetDana.getText().toString());
        binding.tvSumBatas.setText(binding.etBatasWaktu.getText().toString());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProgram() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        String namaPic = binding.etNamaPIC.getText().toString().trim();
        String whatsapp = binding.etWhatsapp.getText().toString().trim();

        if (TextUtils.isEmpty(namaPic)) {
            binding.etNamaPIC.setError("Nama PIC wajib diisi");
            return;
        }
        if (TextUtils.isEmpty(whatsapp)) {
            binding.etWhatsapp.setError("No. WhatsApp wajib diisi");
            return;
        }

        binding.btnBuatProgramFinal.setEnabled(false);
        binding.btnBuatProgramFinal.setText("Memverifikasi...");

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if (role != null && (role.contains("mitra") || role.contains("admin"))) {
                            uploadImageAndSave();
                        } else {
                            binding.btnBuatProgramFinal.setEnabled(true);
                            binding.btnBuatProgramFinal.setText("Buat Program");
                            Toast.makeText(getContext(), "Maaf, hanya akun Mitra yang dapat membuat program.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        binding.btnBuatProgramFinal.setEnabled(true);
                        binding.btnBuatProgramFinal.setText("Buat Program");
                        Toast.makeText(getContext(), "Data profil tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.btnBuatProgramFinal.setEnabled(true);
                    binding.btnBuatProgramFinal.setText("Buat Program");
                    Toast.makeText(getContext(), "Gagal verifikasi role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndSave() {
        binding.btnBuatProgramFinal.setText("Mengunggah Foto...");
        String fileName = "program_" + UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("program_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    performFirestoreSave(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    binding.btnBuatProgramFinal.setEnabled(true);
                    binding.btnBuatProgramFinal.setText("Buat Program");
                    Toast.makeText(getContext(), "Gagal unggah foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void performFirestoreSave(String imageUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;
        
        String userId = currentUser.getUid();
        binding.btnBuatProgramFinal.setText("Menyimpan Data...");

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String namaPembuat = "Mitra";
                    if (documentSnapshot.exists()) {
                        namaPembuat = documentSnapshot.getString("nama");
                        if (namaPembuat == null || namaPembuat.isEmpty()) {
                            namaPembuat = documentSnapshot.getString("username");
                        }
                    }
                    
                    final String finalNamaPembuat = namaPembuat;

                    Map<String, Object> program = new HashMap<>();
                    program.put("nama", binding.etNamaProgram.getText().toString().trim());
                    program.put("tipe", selectedKategori);
                    program.put("target", binding.etTargetDana.getText().toString().trim());
                    program.put("batas_waktu", binding.etBatasWaktu.getText().toString().trim());
                    program.put("deskripsi", binding.etDeskripsiProgram.getText().toString().trim());
                    program.put("penerima_manfaat", binding.etPenerimaManfaat.getText().toString().trim());
                    program.put("rencana_penggunaan", binding.etRencanaDana.getText().toString().trim());
                    program.put("nama_pic", binding.etNamaPIC.getText().toString().trim());
                    program.put("no_whatsapp", binding.etWhatsapp.getText().toString().trim());
                    program.put("imageUrl", imageUrl);
                    program.put("status", "Menunggu Review");
                    program.put("terkumpul", 0);
                    program.put("dibuat_oleh", userId);
                    program.put("dibuat_oleh_nama", finalNamaPembuat);
                    program.put("created_at", FieldValue.serverTimestamp());

                    db.collection("programs")
                            .add(program)
                            .addOnSuccessListener(documentReference -> {
                                showSuccess(program);
                            })
                            .addOnFailureListener(e -> {
                                binding.btnBuatProgramFinal.setEnabled(true);
                                binding.btnBuatProgramFinal.setText("Buat Program");
                                Toast.makeText(getContext(), "Gagal simpan ke database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    saveWithDefaultName(imageUrl, userId);
                });
    }

    private void saveWithDefaultName(String imageUrl, String userId) {
        Map<String, Object> program = new HashMap<>();
        program.put("nama", binding.etNamaProgram.getText().toString().trim());
        program.put("tipe", selectedKategori);
        program.put("target", binding.etTargetDana.getText().toString().trim());
        program.put("batas_waktu", binding.etBatasWaktu.getText().toString().trim());
        program.put("deskripsi", binding.etDeskripsiProgram.getText().toString().trim());
        program.put("penerima_manfaat", binding.etPenerimaManfaat.getText().toString().trim());
        program.put("rencana_penggunaan", binding.etRencanaDana.getText().toString().trim());
        program.put("nama_pic", binding.etNamaPIC.getText().toString().trim());
        program.put("no_whatsapp", binding.etWhatsapp.getText().toString().trim());
        program.put("imageUrl", imageUrl);
        program.put("status", "Menunggu Review");
        program.put("terkumpul", 0);
        program.put("dibuat_oleh", userId);
        program.put("dibuat_oleh_nama", "Mitra");
        program.put("created_at", FieldValue.serverTimestamp());

        db.collection("programs")
                .add(program)
                .addOnSuccessListener(documentReference -> showSuccess(program))
                .addOnFailureListener(e -> {
                    binding.btnBuatProgramFinal.setEnabled(true);
                    binding.btnBuatProgramFinal.setText("Buat Program");
                    Toast.makeText(getContext(), "Gagal simpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showSuccess(Map<String, Object> data) {
        binding.tvFinalJudul.setText((String) data.get("nama"));
        binding.tvFinalKategori.setText((String) data.get("tipe"));
        binding.tvFinalTarget.setText((String) data.get("target"));
        
        goToStep(4);

        new Handler().postDelayed(() -> {
            if (isAdded()) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        }, 5000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
