package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.databinding.FragmentFaqBinding;

public class FaqFragment extends Fragment {

    private FragmentFaqBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFaqBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 1. KATEGORI UMUM
        binding.faqUang1.setOnClickListener(v -> toggleFaq(binding.ansUang1, binding.arrowUang1));
        binding.faqUang2.setOnClickListener(v -> toggleFaq(binding.ansUang2, binding.arrowUang2));
        binding.faqUang3.setOnClickListener(v -> toggleFaq(binding.ansUang3, binding.arrowUang3));

        // 2. KATEGORI BARANG
        binding.faqBarang1.setOnClickListener(v -> toggleFaq(binding.ansBarang1, binding.arrowBarang1));
        binding.faqBarang2.setOnClickListener(v -> toggleFaq(binding.ansBarang2, binding.arrowBarang2));
        binding.faqBarang3.setOnClickListener(v -> toggleFaq(binding.ansBarang3, binding.arrowBarang3));

        // 3. KATEGORI AKUN
        binding.faqAkun1.setOnClickListener(v -> toggleFaq(binding.ansAkun1, binding.arrowAkun1));
        binding.faqAkun2.setOnClickListener(v -> toggleFaq(binding.ansAkun2, binding.arrowAkun2));
    }

    private void toggleFaq(TextView ans, TextView arrow) {
        boolean isVisible = ans.getVisibility() == View.VISIBLE;
        
        // Animasi halus saat buka/tutup
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot());
        
        if (isVisible) {
            ans.setVisibility(View.GONE);
            arrow.setText("⌄");
        } else {
            ans.setVisibility(View.VISIBLE);
            arrow.setText("⌃");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
