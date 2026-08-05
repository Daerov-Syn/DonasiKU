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
import com.aplikasiprojeksmt4.adapters.VerifikasiProgramAdapter;
import com.aplikasiprojeksmt4.databinding.PageVerifikasiAjuanprogramAdminBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerifikasiAjuanProgramAdminFragment extends Fragment {

    private PageVerifikasiAjuanprogramAdminBinding binding;
    private FirebaseFirestore db;
    private VerifikasiProgramAdapter adapter;
    private List<Program> programList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageVerifikasiAjuanprogramAdminBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        loadPendingPrograms();
    }

    private void setupRecyclerView() {
        adapter = new VerifikasiProgramAdapter(programList, new VerifikasiProgramAdapter.OnActionListener() {
            @Override
            public void onApprove(Program program) {
                updateProgramStatus(program, "Aktif");
            }

            @Override
            public void onReject(Program program) {
                updateProgramStatus(program, "Ditolak");
            }

            @Override
            public void onDetail(Program program) {
                Bundle bundle = new Bundle();
                bundle.putString("programId", program.getId());
                Navigation.findNavController(requireView()).navigate(R.id.action_verifikasi_ke_detail, bundle);
            }
        });
        binding.rvVerifikasiProgram.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvVerifikasiProgram.setAdapter(adapter);
    }

    private void loadPendingPrograms() {
        db.collection("programs")
                .whereEqualTo("status", "Menunggu Review")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("VerifikasiProgram", "Listen failed.", error);
                        return;
                    }

                    programList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Program p = doc.toObject(Program.class);
                            p.setId(doc.getId());
                            programList.add(p);
                        }
                    }
                    
                    if (programList.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.rvVerifikasiProgram.setVisibility(View.GONE);
                        binding.tvPendingCount.setText("Tidak ada item menunggu persetujuan");
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.rvVerifikasiProgram.setVisibility(View.VISIBLE);
                        binding.tvPendingCount.setText(programList.size() + " item menunggu persetujuanmu");
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void updateProgramStatus(Program program, String newStatus) {
        db.collection("programs").document(program.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Program " + program.getNama() + " " + newStatus, Toast.LENGTH_SHORT).show();
                    // No need to manually remove from list because SnapshotListener will handle it
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
