package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentTarikDanaBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;

public class TarikDanaFragment extends Fragment {

    private FragmentTarikDanaBinding binding;
    private Program program;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            program = (Program) getArguments().getSerializable("program");
        }
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTarikDanaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupBankSpinner();
        setupNominalShortcuts();
        loadTotalSaldoUser();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        binding.btnLanjut.setOnClickListener(v -> {
            String norek = binding.etNoRekening.getText().toString().trim();
            String jumlahStr = binding.etJumlahPenarikan.getText().toString().trim();
            
            if (norek.isEmpty() || jumlahStr.isEmpty()) {
                Toast.makeText(getContext(), "Harap isi semua field wajib", Toast.LENGTH_SHORT).show();
                return;
            }

            long jumlahTarik = Long.parseLong(jumlahStr);
            if (program != null && jumlahTarik > program.getSiap_tarik()) {
                Toast.makeText(getContext(), "Jumlah penarikan melebihi saldo siap tarik program ini", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Toast.makeText(getContext(), "Permintaan penarikan dana berhasil diajukan", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupUI() {
        if (program != null) {
            // Tampilkan data dari objek yang dikirim dulu
            updateProgramUI(program);
            
            // Refresh data program spesifik dari Firestore agar akurat
            db.collection("programs").document(program.getId())
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) return;
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            program = documentSnapshot.toObject(Program.class);
                            if (program != null) {
                                program.setId(documentSnapshot.getId());
                                updateProgramUI(program);
                            }
                        }
                    });
        }
    }

    private void updateProgramUI(Program p) {
        if (binding == null) return;
        String formattedAmount = formatRupiah(p.getSiap_tarik());
        binding.tvMainAmount.setText(formattedAmount);
        binding.tvProgramName.setText(p.getNama());
        binding.etJumlahPenarikan.setText(String.valueOf(p.getSiap_tarik()));
    }

    private void loadTotalSaldoUser() {
        String userId = auth.getUid();
        if (userId == null) return;

        // Ambil semua program yang dibuat oleh user ini
        db.collection("programs")
                .whereEqualTo("dibuat_oleh", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("TarikDana", "Error fetching balance", error);
                        return;
                    }

                    if (value != null) {
                        long totalSiapTarik = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            Long siapTarik = doc.getLong("siap_tarik");
                            if (siapTarik != null) {
                                totalSiapTarik += siapTarik;
                            }
                        }
                        
                        if (binding != null) {
                            String formattedTotal = formatRupiah(totalSiapTarik);
                            binding.tvSaldoSiapTarikSubtitle.setText("Saldo siap tarik: " + formattedTotal);
                        }
                    }
                });
    }

    private String formatRupiah(long amount) {
        return String.format(new Locale("id", "ID"), "Rp %,d", amount).replace(',', '.');
    }

    private void setupBankSpinner() {
        String[] banks = {"BRI", "BCA", "Mandiri", "BNI", "BSI"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, banks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBank.setAdapter(adapter);
    }

    private void setupNominalShortcuts() {
        binding.tv500rb.setOnClickListener(v -> binding.etJumlahPenarikan.setText("500000"));
        binding.tv1jt.setOnClickListener(v -> binding.etJumlahPenarikan.setText("1000000"));
        binding.tv3jt.setOnClickListener(v -> binding.etJumlahPenarikan.setText("3000000"));
        binding.tv5jt.setOnClickListener(v -> binding.etJumlahPenarikan.setText("5000000"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
