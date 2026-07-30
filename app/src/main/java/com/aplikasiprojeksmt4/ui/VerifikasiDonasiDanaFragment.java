package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.PageVerifdonasidanaBinding;

public class VerifikasiDonasiDanaFragment extends Fragment {

    private PageVerifdonasidanaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageVerifdonasidanaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Navigasi ke Detail untuk item yang sudah sukses
        binding.cardDonation1.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_VerifikasiDonasiDanaFragment_to_DetailDonasiFragment));

        binding.cardDonation2.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_VerifikasiDonasiDanaFragment_to_DetailDonasiFragment));

        // Navigasi ke Verifikasi Transfer untuk item yang perlu verifikasi
        binding.cardDonation3.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_VerifikasiDonasiDanaFragment_to_VerifikasiTransferFragment));

        binding.btnVerifTransferItem.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_VerifikasiDonasiDanaFragment_to_VerifikasiTransferFragment));

        // Setup Filter Tabs & Logika Menyembunyikan Kartu
        binding.filterSemua.setOnClickListener(v -> {
            setActiveFilter(v);
            binding.cardDonation1.setVisibility(View.VISIBLE);
            binding.cardDonation2.setVisibility(View.VISIBLE);
            binding.cardDonation3.setVisibility(View.VISIBLE);
        });

        binding.filterSukses.setOnClickListener(v -> {
            setActiveFilter(v);
            binding.cardDonation1.setVisibility(View.VISIBLE);
            binding.cardDonation2.setVisibility(View.VISIBLE);
            binding.cardDonation3.setVisibility(View.GONE); // Sembunyikan yang Perlu Verifikasi
        });

        binding.filterPerluVerifikasi.setOnClickListener(v -> {
            setActiveFilter(v);
            binding.cardDonation1.setVisibility(View.GONE); // Sembunyikan yang Sukses
            binding.cardDonation2.setVisibility(View.GONE);
            binding.cardDonation3.setVisibility(View.VISIBLE);
        });

        binding.filterGagal.setOnClickListener(v -> {
            setActiveFilter(v);
            binding.cardDonation1.setVisibility(View.GONE);
            binding.cardDonation2.setVisibility(View.GONE);
            binding.cardDonation3.setVisibility(View.GONE);
        });
    }

    private void setActiveFilter(View activeView) {
        binding.filterSemua.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterSukses.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterPerluVerifikasi.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterGagal.setBackgroundResource(R.drawable.bg_card_white);

        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_purple);
        binding.filterSemua.setTextColor(primaryColor);
        binding.filterSukses.setTextColor(primaryColor);
        binding.filterPerluVerifikasi.setTextColor(primaryColor);
        binding.filterGagal.setTextColor(primaryColor);

        activeView.setBackgroundResource(R.drawable.bg_header_admin);
        if (activeView instanceof TextView) {
            ((TextView) activeView).setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}