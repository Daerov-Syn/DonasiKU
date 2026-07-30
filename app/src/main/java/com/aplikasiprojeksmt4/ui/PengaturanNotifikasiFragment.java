package com.aplikasiprojeksmt4.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.databinding.FragmentPengaturanNotifikasiBinding;

public class PengaturanNotifikasiFragment extends Fragment {

    private FragmentPengaturanNotifikasiBinding binding;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPengaturanNotifikasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup SharedPreferences untuk menyimpan pilihan (on/off)
        sharedPreferences = requireActivity().getSharedPreferences("NotifSettings", Context.MODE_PRIVATE);

        // Tombol Kembali
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Load status Switch (default-nya true/nyala jika belum disetel)
        binding.switchDonasi.setChecked(sharedPreferences.getBoolean("notif_donasi", true));
        binding.switchPenarikan.setChecked(sharedPreferences.getBoolean("notif_penarikan", true));
        binding.switchProgram.setChecked(sharedPreferences.getBoolean("notif_program", true));
        binding.switchTarget.setChecked(sharedPreferences.getBoolean("notif_target", true));

        // Simpan setiap kali ditekan
        binding.switchDonasi.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notif_donasi", isChecked).apply());

        binding.switchPenarikan.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notif_penarikan", isChecked).apply());

        binding.switchProgram.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notif_program", isChecked).apply());

        binding.switchTarget.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notif_target", isChecked).apply());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}