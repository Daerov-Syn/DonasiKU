package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.adapters.MitraRecommendationAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentSmartMatchingBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmartMatchingFragment extends Fragment {

    private FragmentSmartMatchingBinding binding;
    private FirebaseFirestore db;
    private MitraRecommendationAdapter adapter;
    private List<Program> programList = new ArrayList<>();
    private Program selectedProgram;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSmartMatchingBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        loadRecommendations();

        binding.btnConfirmMitra.setOnClickListener(v -> {
            if (selectedProgram != null) {
                Bundle bundle = new Bundle();
                bundle.putString("programId", selectedProgram.getId());
                Navigation.findNavController(v).navigate(R.id.action_SmartMatchingFragment_to_DetailDonasiBarangFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Pilih mitra terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MitraRecommendationAdapter(programList, program -> {
            selectedProgram = program;
            updateImpactPreview(program);
        });
        binding.rvRekomendasi.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRekomendasi.setAdapter(adapter);
    }

    private void loadRecommendations() {
        db.collection("programs")
                .whereEqualTo("status", "Aktif")
                .whereEqualTo("tipe", "Barang")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        programList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Program p = doc.toObject(Program.class);
                            p.setId(doc.getId());
                            programList.add(p);
                        }

                        // Smart Matching Logic: Sort by urgency (lower progress = higher priority)
                        Collections.sort(programList, (p1, p2) -> {
                            float ratio1 = (float) p1.getTerkumpul() / p1.getTargetValue();
                            float ratio2 = (float) p2.getTerkumpul() / p2.getTargetValue();
                            return Float.compare(ratio1, ratio2);
                        });

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateImpactPreview(Program program) {
        binding.cardImpactPreview.setVisibility(View.VISIBLE);
        binding.tvImpactText.setText("Donasimu akan membantu " + program.getPenerima_count() + " penerima di " + program.getWilayah() + ".");
        
        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivImpactPreview);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
