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
import com.aplikasiprojeksmt4.databinding.FragmentBuktiDonasiBinding;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuktiDonasiFragment extends Fragment {

    private FragmentBuktiDonasiBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBuktiDonasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding == null) return;

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnClose.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_BuktiDonasiFragment_to_HomeFragment);
            } catch (Exception e) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        displayData();

        binding.btnDownload.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Bukti donasi disimpan ke galeri", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayData() {
        if (binding == null) return;

        if (getArguments() != null) {
            String amount = getArguments().getString("amount");
            String amountFormatted = getArguments().getString("amount_formatted");
            String method = getArguments().getString("method");
            String bank = getArguments().getString("bank");
            boolean isAnonymous = getArguments().getBoolean("isAnonymous", false);
            String programName = getArguments().getString("programName");

            // Display amount safely
            if (amountFormatted != null) {
                binding.tvAmount.setText(amountFormatted);
            } else if (amount != null) {
                try {
                    long val = Long.parseLong(amount);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    formatter.setMaximumFractionDigits(0);
                    binding.tvAmount.setText(formatter.format(val));
                } catch (NumberFormatException e) {
                    binding.tvAmount.setText("Rp " + amount);
                }
            }

            // Display method safely
            String methodText = (method != null ? method : "");
            if (bank != null && !bank.isEmpty()) {
                methodText += " " + bank;
            }
            binding.tvMethod.setText(methodText.isEmpty() ? "-" : methodText);

            // Display donor name
            binding.tvDonorName.setText(isAnonymous ? "Anonim" : "Donatur");
            
            // Display program name if available
            if (programName != null && !programName.isEmpty()) {
                binding.tvProgramName.setText(programName);
            }
        }

        // Display current date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
            binding.tvDate.setText(sdf.format(new Date()));
        } catch (Exception e) {
            binding.tvDate.setText("-");
        }
        
        // Display a transaction ID
        binding.tvTransactionId.setText("DKU-" + System.currentTimeMillis() / 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
