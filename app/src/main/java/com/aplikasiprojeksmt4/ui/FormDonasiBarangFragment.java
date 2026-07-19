package com.aplikasiprojeksmt4.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentFormDonasiBarangBinding;
import com.google.android.material.button.MaterialButton;

public class FormDonasiBarangFragment extends Fragment {

    private FragmentFormDonasiBarangBinding binding;
    private String selectedKondisi = "Baru";
    private Uri selectedImageUri;
    private boolean isDropPointSelected = false;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.ivPreviewFoto.setImageURI(selectedImageUri);
                    binding.ivPreviewFoto.setVisibility(View.VISIBLE);
                    binding.layoutPlaceholderFoto.setVisibility(View.GONE);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormDonasiBarangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 1. Kondisi Barang Selection
        binding.btnKondisiBaru.setOnClickListener(v -> selectKondisi("Baru"));
        binding.btnKondisiSangatBaik.setOnClickListener(v -> selectKondisi("Sangat Baik"));
        binding.btnKondisiBaik.setOnClickListener(v -> selectKondisi("Baik"));

        // 2. Foto Barang Upload
        binding.btnUploadFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        // 3. Metode Pengiriman Selection - clicking the card selects it
        binding.cardDropPoint.setOnClickListener(v -> updateMetodeUI(true));
        binding.cardEkspedisi.setOnClickListener(v -> updateMetodeUI(false));

        // Set default selection UI (Ekspedisi)
        updateMetodeUI(false);

        binding.btnJadwalkan.setOnClickListener(v -> {
            if (isDropPointSelected) {
                Navigation.findNavController(v).navigate(R.id.action_FormDonasiBarangFragment_to_DropPointFragment);
            } else {
                Navigation.findNavController(v).navigate(R.id.action_FormDonasiBarangFragment_to_AlamatPenjemputanFragment);
            }
        });
    }

    private void selectKondisi(String kondisi) {
        selectedKondisi = kondisi;
        
        // Reset all buttons to outlined style
        setButtonStyle(binding.btnKondisiBaru, false);
        setButtonStyle(binding.btnKondisiSangatBaik, false);
        setButtonStyle(binding.btnKondisiBaik, false);

        // Set selected button to filled style
        if (kondisi.equals("Baru")) {
            setButtonStyle(binding.btnKondisiBaru, true);
        } else if (kondisi.equals("Sangat Baik")) {
            setButtonStyle(binding.btnKondisiSangatBaik, true);
        } else if (kondisi.equals("Baik")) {
            setButtonStyle(binding.btnKondisiBaik, true);
        }
    }

    private void setButtonStyle(MaterialButton button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            button.setStrokeWidth(0);
        } else {
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            button.setStrokeColorResource(R.color.light_gray);
            button.setStrokeWidth(2);
        }
    }

    private void updateMetodeUI(boolean isDropPoint) {
        isDropPointSelected = isDropPoint;
        
        if (isDropPoint) {
            // Select Drop Point
            binding.cardDropPoint.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.primary_purple));
            binding.rlDropPoint.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_detail));
            binding.rbDropPoint.setChecked(true);
            
            // Unselect Ekspedisi
            binding.cardEkspedisi.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.light_gray));
            binding.rlEkspedisi.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.rbEkspedisi.setChecked(false);
        } else {
            // Select Ekspedisi
            binding.cardEkspedisi.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.primary_purple));
            binding.rlEkspedisi.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_detail));
            binding.rbEkspedisi.setChecked(true);
            
            // Unselect Drop Point
            binding.cardDropPoint.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.light_gray));
            binding.rlDropPoint.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.rbDropPoint.setChecked(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
