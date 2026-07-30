package com.aplikasiprojeksmt4.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentDonasiDanaBinding;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.Locale;

public class DonasiDanaFragment extends Fragment {

    private FragmentDonasiDanaBinding binding;
    private String selectedMethod = "Transfer Bank";
    private String selectedBank = "BCA";
    private long currentAmount = 0;
    private String programName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDonasiDanaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            programName = getArguments().getString("programName");
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupNominalButtons();
        setupPaymentMethodSelection();
        setupAmountInput();

        binding.btnPayNow.setOnClickListener(v -> {
            if (currentAmount < 10000) {
                Toast.makeText(getContext(), "Minimal donasi adalah Rp 10.000", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("amount", String.valueOf(currentAmount));
            bundle.putString("method", selectedMethod);
            bundle.putString("bank", selectedBank);
            bundle.putString("programId", getArguments() != null ? getArguments().getString("programId") : "");
            bundle.putString("programName", programName);
            bundle.putString("message", binding.etMessage.getText().toString());
            bundle.putBoolean("isAnonymous", binding.switchAnonymous.isChecked());

            Navigation.findNavController(v).navigate(R.id.action_DonasiDanaFragment_to_PembayaranFragment, bundle);
        });
    }

    private void setupNominalButtons() {
        binding.btnNominal25k.setOnClickListener(v -> {
            updateAmount(25000);
            updateNominalSelection(binding.btnNominal25k);
        });
        binding.btnNominal50k.setOnClickListener(v -> {
            updateAmount(50000);
            updateNominalSelection(binding.btnNominal50k);
        });
        binding.btnNominal100k.setOnClickListener(v -> {
            updateAmount(100000);
            updateNominalSelection(binding.btnNominal100k);
        });
        binding.btnNominal200k.setOnClickListener(v -> {
            updateAmount(200000);
            updateNominalSelection(binding.btnNominal200k);
        });
        binding.btnNominal500k.setOnClickListener(v -> {
            updateAmount(500000);
            updateNominalSelection(binding.btnNominal500k);
        });
        binding.btnNominalLainnya.setOnClickListener(v -> {
            updateNominalSelection(binding.btnNominalLainnya);
            binding.etCustomNominal.setText("");
            binding.etCustomNominal.requestFocus();
        });
    }

    private void updateNominalSelection(MaterialButton selectedBtn) {
        MaterialButton[] buttons = {
            binding.btnNominal25k, binding.btnNominal50k, binding.btnNominal100k,
            binding.btnNominal200k, binding.btnNominal500k, binding.btnNominalLainnya
        };

        for (MaterialButton btn : buttons) {
            if (btn == selectedBtn) {
                btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_purple)));
                btn.setTextColor(Color.WHITE);
                btn.setStrokeWidth(0);
            } else {
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                btn.setTextColor(Color.BLACK);
                btn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                btn.setStrokeWidth(2);
            }
        }
    }

    private void updateAmount(long amount) {
        currentAmount = amount;
        String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(amount);
        binding.etCustomNominal.setText(formatted);
    }

    private void setupAmountInput() {
        binding.etCustomNominal.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    binding.etCustomNominal.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^0-9]", "");
                    if (!cleanString.isEmpty()) {
                        long parsed = Long.parseLong(cleanString);
                        currentAmount = parsed;
                        // Use space as thousands separator as seen in screenshot "10 000"
                        String formatted = NumberFormat.getNumberInstance(Locale.US).format(parsed).replace(",", " ");
                        current = formatted;
                        binding.etCustomNominal.setText(formatted);
                        binding.etCustomNominal.setSelection(formatted.length());
                        
                        checkAndResetNominalButtons(parsed);
                    } else {
                        currentAmount = 0;
                        current = "";
                        binding.etCustomNominal.setText("");
                    }

                    binding.etCustomNominal.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkAndResetNominalButtons(long amount) {
        if (amount == 25000) updateNominalSelection(binding.btnNominal25k);
        else if (amount == 50000) updateNominalSelection(binding.btnNominal50k);
        else if (amount == 100000) updateNominalSelection(binding.btnNominal100k);
        else if (amount == 200000) updateNominalSelection(binding.btnNominal200k);
        else if (amount == 500000) updateNominalSelection(binding.btnNominal500k);
        else updateNominalSelection(binding.btnNominalLainnya);
    }

    private void setupPaymentMethodSelection() {
        binding.rgBanks.setOnCheckedChangeListener((group, checkedId) -> updateSelectedBank());

        binding.btnMethodTransfer.setOnClickListener(v -> {
            binding.rbTransfer.setChecked(true);
            binding.rbQRIS.setChecked(false);
            binding.llBankList.setVisibility(View.VISIBLE);
            selectedMethod = "Transfer Bank";
            updateSelectedBank();
            
            binding.btnMethodTransfer.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_purple)));
            binding.btnMethodQRIS.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        binding.btnMethodQRIS.setOnClickListener(v -> {
            binding.rbQRIS.setChecked(true);
            binding.rbTransfer.setChecked(false);
            binding.llBankList.setVisibility(View.GONE);
            selectedMethod = "QRIS";
            selectedBank = "QRIS";
            binding.tvSelectedBank.setText("");
            
            binding.btnMethodQRIS.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_purple)));
            binding.btnMethodTransfer.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT));
        });

        binding.rbTransfer.setOnClickListener(v -> binding.btnMethodTransfer.performClick());
        binding.rbQRIS.setOnClickListener(v -> binding.btnMethodQRIS.performClick());

        // Default selection
        binding.rbBCA.setChecked(true);
        binding.btnMethodTransfer.performClick();
    }

    private void updateSelectedBank() {
        int checkedId = binding.rgBanks.getCheckedRadioButtonId();
        if (checkedId == R.id.rbBCA) {
            selectedBank = "BCA";
            binding.ivSelectedBankIcon.setImageResource(R.drawable.ic_bca);
        } else if (checkedId == R.id.rbBNI) {
            selectedBank = "BNI";
            binding.ivSelectedBankIcon.setImageResource(R.drawable.ic_bni);
        } else if (checkedId == R.id.rbBRI) {
            selectedBank = "BRI";
            binding.ivSelectedBankIcon.setImageResource(R.drawable.ic_bri);
        }
        binding.tvSelectedBank.setText(selectedBank);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
