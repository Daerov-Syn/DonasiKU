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
import com.aplikasiprojeksmt4.databinding.FragmentTambahRekeningBinding;

public class TambahRekeningFragment extends Fragment {

    private FragmentTambahRekeningBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTambahRekeningBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackTambah.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnBatal.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnSimpan.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Rekening Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show();
            // Kembali ke halaman sebelumnya setelah simpan
            Navigation.findNavController(v).navigateUp();
        });
    }
}