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
import com.aplikasiprojeksmt4.databinding.PageVerifdonasibarangBinding;

public class VerifikasiDonasiBarangFragment extends Fragment {

    private PageVerifdonasibarangBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageVerifdonasibarangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Setup Filter Tabs logic
        binding.filterSemua.setOnClickListener(this::setActiveFilter);
        binding.filterTerjadwal.setOnClickListener(this::setActiveFilter);
        binding.filterTersalurkan.setOnClickListener(this::setActiveFilter);

        // Navigasi ke Detail Donasi Barang untuk semua item
        View.OnClickListener toDetail = v -> 
            Navigation.findNavController(v).navigate(R.id.action_VerifikasiDonasiBarangFragment_to_DetailDonasiBarangFragment);

        binding.cardDonasi1.setOnClickListener(toDetail);
        binding.cardDonasi2.setOnClickListener(toDetail);
        binding.cardDonasi3.setOnClickListener(toDetail);
        binding.cardDonasi4.setOnClickListener(toDetail);
        binding.cardDonasi5.setOnClickListener(toDetail);
    }

    private void setActiveFilter(View activeView) {
        binding.filterSemua.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterTerjadwal.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterTersalurkan.setBackgroundResource(R.drawable.bg_card_white);

        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_purple);
        binding.filterSemua.setTextColor(primaryColor);
        binding.filterTerjadwal.setTextColor(primaryColor);
        binding.filterTersalurkan.setTextColor(primaryColor);

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
