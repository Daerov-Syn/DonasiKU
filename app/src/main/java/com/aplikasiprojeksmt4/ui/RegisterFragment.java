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
import com.aplikasiprojeksmt4.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toggle Password Visibility
        binding.ivPasswordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                binding.etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            isPasswordVisible = !isPasswordVisible;
            binding.etRegPassword.setSelection(binding.etRegPassword.getText().length());
        });

        binding.tvToLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_RegisterFragment_to_LoginFragment);
        });

        binding.btnRegisterSubmit.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        if (binding == null) return;

        String username = binding.etRegUsername.getText().toString().trim();
        String email = binding.etRegEmail.getText().toString().trim();
        String password = binding.etRegPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etRegUsername.setError("Username tidak boleh kosong");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegEmail.setError("Email tidak valid");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            binding.etRegPassword.setError("Password minimal 6 karakter");
            return;
        }

        binding.btnRegisterSubmit.setEnabled(false);
        binding.btnRegisterSubmit.setText("Loading...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), username, email);
                        }
                    } else {
                        if (binding != null) {
                            binding.btnRegisterSubmit.setEnabled(true);
                            binding.btnRegisterSubmit.setText("Daftar");
                            Toast.makeText(getContext(), "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String userId, String username, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("nama", username);
        user.put("email", email);
        user.put("role", "user"); // Role default adalah user
        user.put("emailVerified", false);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    if (binding == null) return;
                    Toast.makeText(getContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).navigate(R.id.action_RegisterFragment_to_LoginFragment);
                })
                .addOnFailureListener(e -> {
                    if (binding == null) return;
                    binding.btnRegisterSubmit.setEnabled(true);
                    binding.btnRegisterSubmit.setText("Daftar");
                    Toast.makeText(getContext(), "Error Simpan Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
