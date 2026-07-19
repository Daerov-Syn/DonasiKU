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
import com.aplikasiprojeksmt4.databinding.FragmentDonasibarangBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DonasiBarangFragment extends Fragment {

    private FragmentDonasibarangBinding binding;
    private FirebaseFirestore db;
    private ProgramAdapter adapter;
    private List<Program> programList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDonasibarangBinding.inflate(inflater, container, false);
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
        binding.rvDonasiBarang.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDonasiBarang.setAdapter(adapter);

        adapter.setOnItemClickListener(program -> {
            Bundle bundle = new Bundle();
            bundle.putString("programId", program.getId());
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_DonasiBarangFragment_to_DetailDonasiBarangFragment,
                    bundle
            );
        });
    }

    private void loadPrograms() {
        Log.d("DonasiBarang", "Memulai load data...");
        
        // Ambil SEMUA data programs dan filter di Java untuk menghindari masalah Indexing/Case-Sensitive
        db.collection("programs")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("DonasiBarang", "Firestore Error: " + error.getMessage());
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Gagal: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    if (value != null) {
                        programList.clear();
                        Log.d("DonasiBarang", "Total dokumen ditemukan: " + value.size());
                        
                        for (QueryDocumentSnapshot doc : value) {
                            // Cek field secara manual untuk keamanan
                            String tipe = doc.getString("tipe");
                            String status = doc.getString("status");
                            
                            // Log tiap dokumen untuk debug
                            Log.d("DonasiBarang", "Doc ID: " + doc.getId() + " | Tipe: " + tipe + " | Status: " + status);

                            // Filter Case-Insensitive untuk "Barang"
                            if (tipe != null && tipe.trim().equalsIgnoreCase("Barang")) {
                                // Tampilkan yang belum Selesai
                                if (status == null || !status.equalsIgnoreCase("Selesai")) {
                                    try {
                                        Program p = doc.toObject(Program.class);
                                        if (p != null) {
                                            p.setId(doc.getId());
                                            programList.add(p);
                                        }
                                    } catch (Exception e) {
                                        Log.e("DonasiBarang", "Error mapping program: " + doc.getId(), e);
                                    }
                                }
                            }
                        }
                        
                        adapter.notifyDataSetChanged();
                        Log.d("DonasiBarang", "Data yang ditampilkan: " + programList.size());
                        
                        if (programList.isEmpty() && isAdded()) {
                            Toast.makeText(getContext(), "Tidak ada program Donasi Barang", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
