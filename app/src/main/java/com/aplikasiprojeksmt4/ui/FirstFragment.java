package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.aplikasiprojeksmt4.BuildConfig;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentFirstBinding;
import com.aplikasiprojeksmt4.utils.UpdateManager;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private UpdateManager updateManager;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        updateManager = new UpdateManager(requireContext());
        
        // Menampilkan versi aplikasi saat ini
        String versionText = "Versi " + BuildConfig.VERSION_NAME;
        binding.tvAppVersion.setText(versionText);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnNext.setOnClickListener(v -> {
            // Disable button during check
            binding.btnNext.setEnabled(false);
            
            // Check for updates before proceeding
            updateManager.checkForUpdates(new UpdateManager.OnUpdateCheckListener() {
                @Override
                public void onNoUpdate() {
                    // Re-enable button and navigate if no update is found
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            binding.btnNext.setEnabled(true);
                            NavHostFragment.findNavController(FirstFragment.this)
                                    .navigate(R.id.action_FirstFragment_to_WelcomeFragment);
                        });
                    }
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
