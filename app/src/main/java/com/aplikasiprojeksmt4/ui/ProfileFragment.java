package com.aplikasiprojeksmt4.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentProfileBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();

        binding.llDataDiri.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_DataDiriFragment)
        );

        binding.llRiwayat.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HistoryFragment)
        );

        binding.llLaporanRealTime.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_LaporanRealTimeFragment)
        );

        // Menghubungkan ke Homepage Admin (Hanya muncul jika role admin)
        binding.llAdministrator.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HomepageAdminFragment)
        );

        // Menghubungkan ke Manajemen Mitra (Admin & Mitra)
        binding.llManajemenMitra.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_ManajemenProgramFragment)
        );

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            // Ganti SplashActivity atau LoginPage sesuai alur aplikasi Anda
            Intent intent = new Intent(requireActivity(), WelcomeFragment.class); 
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        userListener = db.collection("users").document(userId).addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;

            if (value != null && value.exists()) {
                String nama = value.getString("nama");
                String email = value.getString("email");
                String photoUrl = value.getString("profile_photo");
                String role = value.getString("role");

                binding.tvProfileName.setText(nama != null ? nama : "User");
                binding.tvProfileEmail.setText(email != null ? email : "");

                // Cek Role Admin dari Database
                if ("admin".equals(role)) {
                    binding.llAdministrator.setVisibility(View.VISIBLE);
                    binding.viewSeparatorAdmin.setVisibility(View.VISIBLE);
                } else {
                    binding.llAdministrator.setVisibility(View.GONE);
                    binding.viewSeparatorAdmin.setVisibility(View.GONE);
                }

                // Cek Role Admin dan Mitra untuk Manajemen Mitra
                if ("admin".equals(role) || "mitra".equals(role)) {
                    binding.llManajemenMitra.setVisibility(View.VISIBLE);
                    binding.viewSeparatorMitra.setVisibility(View.VISIBLE);
                } else {
                    binding.llManajemenMitra.setVisibility(View.GONE);
                    binding.viewSeparatorMitra.setVisibility(View.GONE);
                }

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(photoUrl)
                            .circleCrop()
                            .placeholder(R.drawable.group_2)
                            .into(binding.ivProfilePicture);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) {
            userListener.remove();
        }
        binding = null;
    }
}
