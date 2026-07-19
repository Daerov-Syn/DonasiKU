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
import com.aplikasiprojeksmt4.databinding.FragmentPilihBankBinding;

public class PilihBankFragment extends Fragment {

    private FragmentPilihBankBinding binding;
    private String amount;
    private String programId;
    private String message;
    private boolean isAnonymous;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPilihBankBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            amount = getArguments().getString("amount");
            programId = getArguments().getString("programId");
            message = getArguments().getString("message");
            isAnonymous = getArguments().getBoolean("isAnonymous");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnConfirmBank.setOnClickListener(v -> {
            String selectedBank = "";
            if (binding.rbBCA.isChecked()) selectedBank = "BCA";
            else if (binding.rbBNI.isChecked()) selectedBank = "BNI";
            else if (binding.rbBRI.isChecked()) selectedBank = "BRI";
            else if (binding.rbQRIS.isChecked()) {
                navigateToPembayaran("QRIS", "QRIS");
                return;
            }

            if (selectedBank.isEmpty()) {
                Toast.makeText(getContext(), "Pilih bank terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            navigateToPembayaran("Transfer Bank", selectedBank);
        });
    }

    private void navigateToPembayaran(String method, String bank) {
        Bundle bundle = new Bundle();
        bundle.putString("amount", amount);
        bundle.putString("method", method);
        bundle.putString("bank", bank);
        bundle.putString("programId", programId);
        bundle.putString("message", message);
        bundle.putBoolean("isAnonymous", isAnonymous);

        Navigation.findNavController(requireView()).navigate(R.id.action_PilihBankFragment_to_PembayaranFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
