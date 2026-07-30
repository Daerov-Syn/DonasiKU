package com.aplikasiprojeksmt4.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentPembayaranBinding;
import com.aplikasiprojeksmt4.models.DonaturDana;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PembayaranFragment extends Fragment {

    private FragmentPembayaranBinding binding;
    private FirebaseFirestore db;
    private String amount;
    private String method;
    private String bank;
    private String programId;
    private String programName;
    private String donorName = "Memuat...";
    private String message;
    private boolean isAnonymous;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPembayaranBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            amount = getArguments().getString("amount");
            method = getArguments().getString("method");
            bank = getArguments().getString("bank");
            programId = getArguments().getString("programId");
            programName = getArguments().getString("programName");
            message = getArguments().getString("message");
            isAnonymous = getArguments().getBoolean("isAnonymous", false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        fetchUserData();
        displayData();
        setupLayoutByMethod();
        startTimer();

        binding.btnCopyVa.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("VA Number", binding.tvVaNumber.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Nomor VA disalin", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveQR.setOnClickListener(v -> {
            Toast.makeText(getContext(), "QR Code disimpan ke galeri", Toast.LENGTH_SHORT).show();
        });

        binding.btnCheckStatus.setOnClickListener(v -> {
            saveDonationToDatabase(v);
        });

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void fetchUserData() {
        if (isAnonymous) {
            donorName = "Anonim";
            binding.tvDonorName.setText(donorName);
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            donorName = documentSnapshot.getString("nama");
                            if (donorName == null || donorName.isEmpty()) {
                                donorName = "User Tanpa Nama";
                            }
                        } else {
                            donorName = "User Tidak Dikenal";
                        }
                        if (binding != null) {
                            binding.tvDonorName.setText(donorName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        donorName = "Gagal Memuat";
                        if (binding != null) {
                            binding.tvDonorName.setText(donorName);
                        }
                    });
        } else {
            donorName = "Tamu";
            binding.tvDonorName.setText(donorName);
        }
    }

    private void saveDonationToDatabase(View view) {
        if (programId == null || programId.isEmpty()) {
            Toast.makeText(getContext(), "Error: ID Program tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnCheckStatus.setEnabled(false);
        binding.btnCheckStatus.setText("Memproses...");

        long nominal = 0;
        try {
            nominal = Long.parseLong(amount);
        } catch (Exception e) {
            nominal = 0;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        
        // Create donation record
        DonaturDana donasi = new DonaturDana();
        donasi.setUserId(userId);
        donasi.setNamaDonatur(donorName);
        donasi.setNominal(nominal);
        donasi.setPesan(message);
        donasi.setProgramId(programId);
        donasi.setProgramNama(programName); // Save program name for history display
        donasi.setStatus("Berhasil");
        donasi.setMetodePengiriman(method + (bank != null && !bank.isEmpty() ? " - " + bank : ""));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        donasi.setTanggalDonasi(sdf.format(new Date()));

        // Use batch to update program and save donation atomically
        WriteBatch batch = db.batch();
        
        // 1. Add to donatur_dana collection
        String newDonasiId = db.collection("donatur_dana").document().getId();
        donasi.setId(newDonasiId);
        batch.set(db.collection("donatur_dana").document(newDonasiId), donasi);

        // 2. Update program terkumpul and donatur_count
        batch.update(db.collection("programs").document(programId),
                "terkumpul", FieldValue.increment(nominal),
                "donatur_count", FieldValue.increment(1));

        batch.commit().addOnSuccessListener(aVoid -> {
            if (getContext() == null) return;
            
            Bundle bundle = new Bundle();
            if (getArguments() != null) {
                bundle.putAll(getArguments());
                if (amount != null) {
                    try {
                        long val = Long.parseLong(amount);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                        formatter.setMaximumFractionDigits(0);
                        bundle.putString("amount_formatted", formatter.format(val));
                    } catch (Exception e) {
                        bundle.putString("amount_formatted", "Rp " + amount);
                    }
                }
            }
            
            Navigation.findNavController(view).navigate(R.id.action_PembayaranFragment_to_StatusPembayaranFragment, bundle);
            
        }).addOnFailureListener(e -> {
            if (getContext() == null) return;
            binding.btnCheckStatus.setEnabled(true);
            binding.btnCheckStatus.setText("Saya Sudah Bayar");
            Toast.makeText(getContext(), "Gagal menyimpan donasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupLayoutByMethod() {
        if ("QRIS".equalsIgnoreCase(method)) {
            binding.tvHeaderTitle.setText("Pembayaran QRIS");
            binding.cardQRIS.setVisibility(View.VISIBLE);
            binding.cardBank.setVisibility(View.GONE);
            binding.ivQRCode.setImageResource(R.drawable.ic_qris); // Set placeholder or actual QR
        } else {
            binding.tvHeaderTitle.setText("Pembayaran");
            binding.cardQRIS.setVisibility(View.GONE);
            binding.cardBank.setVisibility(View.VISIBLE);
            String bankName = (bank != null && !bank.isEmpty() ? bank : "BCA");
            binding.tvBankName.setText("Bank " + bankName);
            
            // Set Bank Logo
            if (bankName.equalsIgnoreCase("BCA")) {
                binding.ivBankLogo.setImageResource(R.drawable.ic_bca);
            } else if (bankName.equalsIgnoreCase("BNI")) {
                binding.ivBankLogo.setImageResource(R.drawable.ic_bni);
            } else if (bankName.equalsIgnoreCase("BRI")) {
                binding.ivBankLogo.setImageResource(R.drawable.ic_bri);
            }
            binding.ivBankLogo.setImageTintList(null); // Remove tint to show original logo colors
        }
    }

    private void displayData() {
        if (amount != null) {
            try {
                long val = Long.parseLong(amount);
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatter.setMaximumFractionDigits(0);
                binding.tvTotalAmount.setText(formatter.format(val));
            } catch (Exception e) {
                binding.tvTotalAmount.setText("Rp " + amount);
            }
        }

        binding.tvProgramName.setText(programName != null ? programName : "Donasi Peduli Sesama");
        binding.tvDonorName.setText(donorName);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        binding.tvDeadline.setText(sdf.format(cal.getTime()));
        
        String vaPrefix = "1230";
        if (bank != null) {
            if (bank.equalsIgnoreCase("BCA")) vaPrefix = "8000";
            else if (bank.equalsIgnoreCase("BNI")) vaPrefix = "8800";
            else if (bank.equalsIgnoreCase("BRI")) vaPrefix = "1230";
        }
        
        String va = vaPrefix + " 0858 " + (int)(Math.random() * 9000 + 1000) + " " + (int)(Math.random() * 9000 + 1000);
        binding.tvVaNumber.setText(va);
    }

    private void startTimer() {
        new CountDownTimer(86400000, 1000) { // 24 hours
            public void onTick(long millisUntilFinished) {
                if (binding == null) return;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                binding.tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            public void onFinish() {
                if (binding != null) binding.tvCountdown.setText("00:00:00");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
