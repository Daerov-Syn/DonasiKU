package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentDokumenLegalBinding;

public class DokumenLegalFragment extends Fragment {

    private FragmentDokumenLegalBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDokumenLegalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Set Text Manual karena kita pakai <include>
        binding.cardAkta.tvJenisDokumen.setText("Akta Pendirian");
        binding.cardAkta.tvNamaFile.setText("Akta_GrahaYKP_2018.pdf - 12 Jan 2026");

        binding.cardSk.tvJenisDokumen.setText("SK Kemenkumham");
        binding.cardSk.tvNamaFile.setText("SK_Kemenkumham_2018.pdf - 20 Jan 2026");

        binding.cardNpwp.tvJenisDokumen.setText("NPWP Yayasan");
        binding.cardNpwp.tvNamaFile.setText("NPWP_GrahaYKP.pdf - 2 Feb 2026");

        binding.cardNib.tvJenisDokumen.setText("NIB / Izin Usaha");
        binding.cardNib.tvNamaFile.setText("NIB_GrahaYKP.pdf - 3 Mar 2026");

        // Action Klik Lihat -> Kirim Data
        binding.cardAkta.btnLihat.setOnClickListener(v -> bukaDetail(v, "Akta Pendirian", "Akta_GrahaYKP_2018.pdf", "12 Jan 2026", "Terverifikasi"));
        binding.cardSk.btnLihat.setOnClickListener(v -> bukaDetail(v, "SK Kemenkumham", "SK_Kemenkumham_2018.pdf", "20 Jan 2026", "Terverifikasi"));
        binding.cardNpwp.btnLihat.setOnClickListener(v -> bukaDetail(v, "NPWP Yayasan", "NPWP_GrahaYKP.pdf", "2 Feb 2026", "Terverifikasi"));
        binding.cardNib.btnLihat.setOnClickListener(v -> bukaDetail(v, "NIB / Izin Usaha", "NIB_GrahaYKP.pdf", "3 Mar 2026", "Terverifikasi"));
    }

    private void bukaDetail(View v, String jenis, String file, String tanggal, String status) {
        Bundle bundle = new Bundle();
        bundle.putString("jenis_dok", jenis);
        bundle.putString("nama_file", file);
        bundle.putString("tanggal", tanggal);
        bundle.putString("status", status);
        Navigation.findNavController(v).navigate(R.id.action_DokumenLegal_to_DetailDokumenLegal, bundle);
    }
}