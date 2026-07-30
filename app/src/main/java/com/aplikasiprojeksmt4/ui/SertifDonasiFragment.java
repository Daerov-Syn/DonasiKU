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
import com.aplikasiprojeksmt4.databinding.FragmentSertifdonasiBinding;

public class SertifDonasiFragment extends Fragment {

    private FragmentSertifdonasiBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSertifdonasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnDownloadPdf.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sertifikat sedang diunduh dalam format PDF...", Toast.LENGTH_LONG).show();
        });

        binding.btnHome.setOnClickListener(v -> {
            // Navigasi kembali ke Home (asumsi HomeFragment adalah start destination atau ada di backstack)
            Navigation.findNavController(v).popBackStack(R.id.HomeFragment, false);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
