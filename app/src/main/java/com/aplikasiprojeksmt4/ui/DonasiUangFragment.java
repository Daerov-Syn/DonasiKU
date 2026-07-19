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
import com.aplikasiprojeksmt4.adapters.ProgramAdapter;
import com.aplikasiprojeksmt4.databinding.FragmentDonasiuangBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DonasiUangFragment extends Fragment {

    private FragmentDonasiuangBinding binding;
    private FirebaseFirestore db;
    private ProgramAdapter adapter;
    private List<Program> programList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDonasiuangBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        loadPrograms();
    }

    private void setupRecyclerView() {
        adapter = new ProgramAdapter(programList);
        binding.rvDonasiUang.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonasiUang.setAdapter(adapter);

        adapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putString("programId", program.getId());
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_DonasiUangFragment_to_DetailProgramDonasiUangFragment,
                    bundle
            );
        });
    }

    private void loadPrograms() {
        db.collection("programs")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("DonasiUang", "Gagal mengambil data: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        programList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Program p = doc.toObject(Program.class);
                            if (p != null) {
                                if (p.getTipe() != null && p.getTipe().equalsIgnoreCase("Dana")) {
                                    if (p.getStatus() == null || !p.getStatus().equalsIgnoreCase("Selesai")) {
                                        p.setId(doc.getId());
                                        programList.add(p);
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
