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
import com.aplikasiprojeksmt4.databinding.FragmentHistoryBarangBinding;

public class HistoryBarangFragment extends Fragment {
    private FragmentHistoryBarangBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBarangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        binding.tabSemua.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryBarangFragment_to_HistoryFragment));
        
        binding.tabUang.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryBarangFragment_to_HistoryUangFragment));
        
        binding.tabProses.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryBarangFragment_to_HistoryProsesFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
