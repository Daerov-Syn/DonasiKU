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
import com.aplikasiprojeksmt4.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.tabUang.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryFragment_to_HistoryUangFragment));
        
        binding.tabBarang.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryFragment_to_HistoryBarangFragment));
        
        binding.tabProses.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryFragment_to_HistoryProsesFragment));

        View.OnClickListener toDetail = v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryFragment_to_DetailDonasiFragment);

        binding.cvHistoryItem1.setOnClickListener(toDetail);
        binding.cvHistoryItem2.setOnClickListener(toDetail);
        binding.cvHistoryItem3.setOnClickListener(toDetail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
