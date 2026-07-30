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
import com.aplikasiprojeksmt4.databinding.DetailDonaturAdminBinding;

public class DetailDonaturAdminFragment extends Fragment {

    private DetailDonaturAdminBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DetailDonaturAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> 
            Navigation.findNavController(v).navigateUp()
        );

        // Di sini bisa ditambahkan logika untuk menerima data donatur via arguments
        // dan mengupdate UI serta mengisi RecyclerView riwayat transaksi
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
