package com.aplikasiprojeksmt4.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.aplikasiprojeksmt4.databinding.FragmentKonfirmasiDonasiBarangBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class KonfirmasiDonasiBarangFragment extends Fragment {

    private FragmentKonfirmasiDonasiBarangBinding binding;
    private FirebaseFirestore db;
    private String donationId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentKonfirmasiDonasiBarangBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            donationId = getArguments().getString("donationId");
        }

        loadDonationDetails();

        binding.btnBackHome.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_KonfirmasiDonasiBarangFragment_to_HomeFragment);
        });

        binding.btnSalinResi.setOnClickListener(v -> {
            String resi = binding.tvResiNumber.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Resi", resi);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Nomor Resi disalin", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDonationDetails() {
        if (donationId == null) return;

        db.collection("donatur_barang").document(donationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (binding == null || !isAdded()) return;
                    if (documentSnapshot.exists()) {
                        String programName = documentSnapshot.getString("programNama");
                        String kondisi = documentSnapshot.getString("kondisi");
                        String metode = documentSnapshot.getString("metodePengiriman");
                        String alamat = documentSnapshot.getString("alamatPenjemputan");
                        String tanggal = documentSnapshot.getString("tanggalDonasi");
                        
                        binding.tvKategoriBarang.setText(programName != null ? programName : "Donasi Barang");
                        binding.tvKondisiBarang.setText(kondisi);
                        binding.tvMetodeDetail.setText(metode);
                        binding.tvAlamatJemput.setText(alamat);
                        binding.tvIdTransaksi.setText("ID: #" + donationId.substring(0, Math.min(8, donationId.length())).toUpperCase());
                        
                        if (tanggal != null) {
                            binding.tvStatusWaktu.setText(tanggal);
                        }
                        
                        if (metode != null && metode.contains("Drop Point")) {
                            binding.cardResi.setVisibility(View.GONE);
                            binding.layoutOngkosKirim.setVisibility(View.GONE);
                            binding.tvSuccessSubtext.setText("Silakan antar barang Anda ke\nlokasi drop point terpilih");
                        } else {
                            binding.cardResi.setVisibility(View.VISIBLE);
                            binding.layoutOngkosKirim.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
