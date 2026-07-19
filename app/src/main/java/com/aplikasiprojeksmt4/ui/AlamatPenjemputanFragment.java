package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentAlamatPenjemputanBinding;
import com.google.android.material.button.MaterialButton;

public class AlamatPenjemputanFragment extends Fragment {

    private FragmentAlamatPenjemputanBinding binding;
    private String selectedEkspedisi = "JNE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlamatPenjemputanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Default selection
        selectEkspedisi(binding.btnJne, "JNE");

        binding.btnJne.setOnClickListener(v -> selectEkspedisi(binding.btnJne, "JNE"));
        binding.btnJnt.setOnClickListener(v -> selectEkspedisi(binding.btnJnt, "J&T"));
        binding.btnSicepat.setOnClickListener(v -> selectEkspedisi(binding.btnSicepat, "SiCepat"));

        binding.btnKonfirmasiPenjemputan.setOnClickListener(v -> {
            // Kita bisa mengirim data menggunakan Bundle jika diperlukan
            Bundle bundle = new Bundle();
            bundle.putString("metode", "Ekspedisi " + selectedEkspedisi);
            bundle.putString("alamat", binding.etAlamatLengkap.getText().toString());
            
            Navigation.findNavController(v).navigate(
                R.id.action_AlamatPenjemputanFragment_to_KonfirmasiDonasiBarangFragment,
                bundle
            );
        });
    }

    private void selectEkspedisi(MaterialButton button, String ekspedisi) {
        selectedEkspedisi = ekspedisi;

        // Reset all buttons to outlined style
        resetButtonStyle(binding.btnJne);
        resetButtonStyle(binding.btnJnt);
        resetButtonStyle(binding.btnSicepat);

        // Set selected button to filled style
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_purple));
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        
        // Update shipping cost based on selection (mock data)
        if (ekspedisi.equals("JNE")) {
            binding.etOngkosKirim.setText("Rp 8.000");
        } else if (ekspedisi.equals("J&T")) {
            binding.etOngkosKirim.setText("Rp 10.000");
        } else {
            binding.etOngkosKirim.setText("Rp 9.000");
        }
    }

    private void resetButtonStyle(MaterialButton button) {
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        button.setStrokeColorResource(R.color.light_gray);
        button.setStrokeWidth(1);
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
