package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.databinding.FragmentBantuanFaqBinding;

public class BantuanFaqFragment extends Fragment {

    private FragmentBantuanFaqBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBantuanFaqBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tombol Kembali
        binding.btnBackFaq.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Tombol Kontak
        binding.btnWa.setOnClickListener(v -> Toast.makeText(getContext(), "Membuka WhatsApp...", Toast.LENGTH_SHORT).show());
        binding.btnTelepon.setOnClickListener(v -> Toast.makeText(getContext(), "Memanggil CS...", Toast.LENGTH_SHORT).show());

        // Setup Klik Buka-Tutup (Accordion)
        binding.faq1Header.setOnClickListener(v -> toggleFaq(binding.faq1Answer, binding.faq1Icon));
        binding.faq2Header.setOnClickListener(v -> toggleFaq(binding.faq2Answer, binding.faq2Icon));
        binding.faq3Header.setOnClickListener(v -> toggleFaq(binding.faq3Answer, binding.faq3Icon));
        binding.faq4Header.setOnClickListener(v -> toggleFaq(binding.faq4Answer, binding.faq4Icon));
        binding.faq5Header.setOnClickListener(v -> toggleFaq(binding.faq5Answer, binding.faq5Icon));
    }

    // Fungsi canggih untuk mengubah status buka/tutup
    private void toggleFaq(View answerView, TextView iconView) {
        if (answerView.getVisibility() == View.VISIBLE) {
            // Jika sedang terbuka, maka tutup
            answerView.setVisibility(View.GONE);
            iconView.setText("▼"); // Panah bawah
        } else {
            // Jika sedang tertutup, maka buka
            answerView.setVisibility(View.VISIBLE);
            iconView.setText("▲"); // Panah atas
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}