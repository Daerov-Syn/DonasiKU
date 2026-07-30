package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentLoginBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivPasswordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            isPasswordVisible = !isPasswordVisible;
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        binding.tvToRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_RegisterFragment);
        });

        binding.btnLoginSubmit.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        if (binding == null) return;

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Masukkan email yang valid");
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password tidak boleh kosong");
            return;
        }

        binding.btnLoginSubmit.setEnabled(false);
        binding.btnLoginSubmit.setText("Loading...");

        // Login menggunakan Firebase Auth terlebih dahulu
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Setelah Auth berhasil, baru ambil data dari Firestore
                            fetchUserData(user.getUid());
                        }
                    } else {
                        handleLoginError("Login Gagal: " + (task.getException() != null ? task.getException().getMessage() : "Periksa koneksi Anda"));
                    }
                });
    }

    private void fetchUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String email = documentSnapshot.getString("email");
                        
                        sessionManager.saveUser(userId, name != null ? name : "User", email != null ? email : "");
                        
                        Toast.makeText(getContext(), "Selamat Datang, " + name, Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_HomeFragment);
                    } else {
                        // Jika document tidak ada, buatkan document baru (opsional, tergantung kebutuhan)
                        handleLoginError("Data profil tidak ditemukan di database.");
                    }
                })
                .addOnFailureListener(e -> handleLoginError("Gagal mengambil data: " + e.getMessage()));
    }

    private void handleLoginError(String message) {
        if (binding != null) {
            binding.btnLoginSubmit.setEnabled(true);
            binding.btnLoginSubmit.setText("Masuk");
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
