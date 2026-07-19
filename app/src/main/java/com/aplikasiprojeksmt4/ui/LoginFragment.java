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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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

        String input = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (input.isEmpty()) {
            binding.etEmail.setError("Email atau Nama tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password tidak boleh kosong");
            return;
        }

        binding.btnLoginSubmit.setEnabled(false);
        binding.btnLoginSubmit.setText("Loading...");

        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            performFirebaseAuthLogin(input, password);
        } else {
            db.collection("users")
                    .whereEqualTo("nama", input)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            String email = doc.getString("email");
                            if (email != null) {
                                performFirebaseAuthLogin(email, password);
                            } else {
                                handleLoginError("Email tidak ditemukan untuk nama tersebut.");
                            }
                        } else {
                            handleLoginError("Nama tidak ditemukan di database.");
                        }
                    })
                    .addOnFailureListener(e -> handleLoginError("Error: " + e.getMessage()));
        }
    }

    private void performFirebaseAuthLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkVerificationAndProceed(user);
                        }
                    } else {
                        checkFallbackFirestoreLogin(email, password);
                    }
                });
    }

    private void checkFallbackFirestoreLogin(String email, String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            checkVerificationAndProceed(user);
                                        }
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            handleLoginError("Email sudah terdaftar. Pastikan password benar.");
                                        } else {
                                            handleLoginError("Gagal sinkronisasi: " + task.getException().getMessage());
                                        }
                                    }
                                });
                    } else {
                        handleLoginError("Email atau Password salah.");
                    }
                })
                .addOnFailureListener(e -> handleLoginError("Error Database: " + e.getMessage()));
    }

    private void checkVerificationAndProceed(FirebaseUser user) {
        // Izinkan login meskipun belum verifikasi. 
        // Verifikasi akan ditangani di halaman Data Diri.
        fetchUserDataFromFirestore(user.getUid());
    }

    private void fetchUserDataFromFirestore(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (binding == null) return;
                    
                    if (documentSnapshot.exists()) {
                        completeLogin(documentSnapshot);
                    } else {
                        // Jika tidak ditemukan dengan UID, cari berdasarkan email (untuk akun lama)
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.getEmail() != null) {
                            findAndMigrateOldUser(user);
                        } else {
                            handleLoginError("Data profil tidak ditemukan.");
                        }
                    }
                })
                .addOnFailureListener(e -> handleLoginError("Gagal memuat data: " + e.getMessage()));
    }

    private void findAndMigrateOldUser(FirebaseUser user) {
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot oldDoc = queryDocumentSnapshots.getDocuments().get(0);
                        // Migrasi data ke document dengan ID = UID
                        Map<String, Object> userData = oldDoc.getData();
                        if (userData == null) userData = new HashMap<>();
                        userData.put("id", user.getUid());

                        db.collection("users").document(user.getUid()).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    // Berhasil migrasi, ambil data terbaru
                                    db.collection("users").document(user.getUid()).get()
                                            .addOnSuccessListener(this::completeLogin);
                                })
                                .addOnFailureListener(e -> handleLoginError("Gagal migrasi data: " + e.getMessage()));
                    } else {
                        handleLoginError("Profil tidak ditemukan di database.");
                    }
                })
                .addOnFailureListener(e -> handleLoginError("Error pencarian data lama: " + e.getMessage()));
    }

    private void completeLogin(DocumentSnapshot userDoc) {
        String userId = userDoc.getId();
        String username = userDoc.getString("nama");
        String email = userDoc.getString("email");
        
        sessionManager.saveUser(userId, username != null ? username : "User", email != null ? email : "");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            Toast.makeText(getContext(), "Login Berhasil. Silakan verifikasi email Anda di Data Diri.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
        }
        
        NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_HomeFragment);
    }

    private void handleLoginError(String message) {
        if (binding != null) {
            binding.btnLoginSubmit.setEnabled(true);
            binding.btnLoginSubmit.setText("Masuk");
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
