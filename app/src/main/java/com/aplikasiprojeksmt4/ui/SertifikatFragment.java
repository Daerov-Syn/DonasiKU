package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.databinding.FragmentSertifikatBinding;
import com.aplikasiprojeksmt4.R;

public class SertifikatFragment extends Fragment {

    private FragmentSertifikatBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSertifikatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Fungsi Tombol Kembali (kembali ke profil)
        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        // 2. Klik pada item untuk membuka sertifikat
        binding.cardSertifikat1.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_SertifikatFragment_to_SertifDonasiFragment);
        });

        binding.cardSertifikat2.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_SertifikatFragment_to_SertifDonasiFragment);
        });

        binding.cardSertifikat3.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_SertifikatFragment_to_SertifDonasiFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}