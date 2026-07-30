package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.databinding.FragmentDetailDokumenLegalBinding;

public class DetailDokumenLegalFragment extends Fragment {

    private FragmentDetailDokumenLegalBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailDokumenLegalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Tangkap Data dari Halaman Sebelumnya
        if (getArguments() != null) {
            String jenis = getArguments().getString("jenis_dok", "-");
            String file = getArguments().getString("nama_file", "-");
            String tanggal = getArguments().getString("tanggal", "-");
            String status = getArguments().getString("status", "-");

            // Pasang datanya ke UI
            binding.tvHeaderTitle.setText(jenis);
            binding.tvHeaderSubtitle.setText(file);

            binding.tvDetailNamaFile.setText(file);
            binding.tvDetailTanggal.setText("Diunggah: " + tanggal);
            binding.tvDetailStatus.setText(status);

            binding.tvInfoJenis.setText(jenis);
            binding.tvInfoFile.setText(file);
            binding.tvInfoTanggal.setText(tanggal);
            binding.tvInfoStatus.setText(status);
        }
    }
}