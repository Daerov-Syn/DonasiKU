package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentRekeningBankBinding;
import com.aplikasiprojeksmt4.models.RekeningBank;
import java.util.ArrayList;
import java.util.List;

public class RekeningBankFragment extends Fragment {

    private FragmentRekeningBankBinding binding;
    private RekeningBankAdapter adapter;
    private List<RekeningBank> listRekening;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRekeningBankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Navigasi ke halaman Tambah
        binding.btnTambahRekeningBaru.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_RekeningBankFragment_to_TambahRekeningFragment)
        );

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        listRekening = new ArrayList<>();
        listRekening.add(new RekeningBank("1", "BRI", "1234-5678-9012-3456", "Yayasan Peduli Bersama", true));
        listRekening.add(new RekeningBank("2", "BCA", "0987-6543-2109-8765", "Yayasan Peduli Bersama", false));
        listRekening.add(new RekeningBank("3", "BNI", "1111-2222-3333-4444", "Yayasan Peduli Bersama", false));

        adapter = new RekeningBankAdapter(listRekening, new RekeningBankAdapter.OnRekeningClickListener() {
            @Override
            public void onDeleteClick(int position) {
                // Logika Hapus: Hilangkan dari list lalu beritahu adapter
                listRekening.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, listRekening.size());
            }

            @Override
            public void onSetUtamaClick(int position) {
                // Logika Set Utama: Matikan semua yang utama, lalu hidupkan yang diklik
                for (RekeningBank rb : listRekening) {
                    rb.setUtama(false);
                }
                listRekening.get(position).setUtama(true);

                // Pindahkan yang utama ke posisi paling atas
                RekeningBank terpilih = listRekening.remove(position);
                listRekening.add(0, terpilih);

                adapter.notifyDataSetChanged();
            }
        });

        binding.rvRekeningBank.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRekeningBank.setAdapter(adapter);
    }
}