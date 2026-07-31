package com.aplikasiprojeksmt4.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.databinding.FragmentHistoryBinding;
import com.aplikasiprojeksmt4.models.Riwayat;
import com.aplikasiprojeksmt4.adapters.RiwayatAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private RiwayatAdapter riwayatAdapter;
    private List<Riwayat> allRiwayatList = new ArrayList<>(); // Penyimpan semua data dari database
    private List<Riwayat> filteredList = new ArrayList<>();   // Data yang ditampilkan setelah difilter

    // Tambahkan variabel Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentActiveTab = "Semua"; // Menyimpan status tab saat ini

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Setup awal RecyclerView agar tidak kosong atau error
        riwayatAdapter = new RiwayatAdapter(filteredList);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(riwayatAdapter);

        // Panggil data langsung dari Firebase
        fetchRiwayatFromDatabase();

        // Logika Klik Tab / Pagination
        binding.tabSemua.setOnClickListener(v -> {
            setActiveTab(binding.tabSemua);
            currentActiveTab = "Semua";
            filterData(currentActiveTab);
        });

        binding.tabUang.setOnClickListener(v -> {
            setActiveTab(binding.tabUang);
            currentActiveTab = "Uang";
            filterData(currentActiveTab);
        });

        binding.tabBarang.setOnClickListener(v -> {
            setActiveTab(binding.tabBarang);
            currentActiveTab = "Barang";
            filterData(currentActiveTab);
        });

        binding.tabProses.setOnClickListener(v -> {
            setActiveTab(binding.tabProses);
            currentActiveTab = "Proses";
            filterData(currentActiveTab);
        });
    }

    // FUNGSI BARU: Mengambil data dari Firestore (Sesuai Database Anda)
    private void fetchRiwayatFromDatabase() {
        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(getContext(), "Anda belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mengambil dari collection "donatur_dana" sesuai screenshot Anda
        db.collection("donatur_dana")
                .whereEqualTo("userId", userId) // Sesuai nama field di Firebase
                .orderBy("timestamp", Query.Direction.DESCENDING) // Mengurutkan dari yang terbaru
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null) {
                        allRiwayatList.clear(); // Bersihkan list lama

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            // Ambil field sesuai persis dengan teks di kolom Firebase Anda
                            String judul = doc.getString("programNama");
                            String tanggal = doc.getString("tanggalDonasi");
                            String statusRaw = doc.getString("status");

                            // Nominal berupa Angka (Number), ambil pakai getLong
                            Long nominalDb = doc.getLong("nominal");
                            String nominalFormat = "-";
                            if (nominalDb != null) {
                                // Format angka menjadi ada pemisah ribuan (titik) dan awalan Rp
                                nominalFormat = "Rp " + String.format("%,d", nominalDb).replace(',', '.');
                            }

                            // Konversi Status Firebase ke Status UI (Tersalurkan/Diproses)
                            String statusFinal = "Diproses"; // Default jika status tidak dikenali
                            if (statusRaw != null) {
                                if (statusRaw.equalsIgnoreCase("Berhasil") || statusRaw.equalsIgnoreCase("Success")) {
                                    statusFinal = "Tersalurkan";
                                } else {
                                    statusFinal = "Diproses";
                                }
                            }

                            // Kategori (Karena dari donatur_dana, kita set "Uang" dulu)
                            String kategori = "Uang";

                            // Antisipasi data kosong/null agar tidak crash
                            judul = (judul != null) ? judul : "Tanpa Judul";
                            tanggal = (tanggal != null) ? tanggal : "-";

                            // Masukkan ke dalam Model
                            allRiwayatList.add(new Riwayat(judul, tanggal, statusFinal, nominalFormat, kategori));
                        }

                        // Update tampilan UI sesuai tab yang sedang diklik
                        filterData(currentActiveTab);
                    }
                });
    }

    // Fungsi untuk menyaring daftar berdasarkan tab
    private void filterData(String kategori) {
        filteredList.clear();

        if (kategori.equals("Semua")) {
            filteredList.addAll(allRiwayatList);
        } else if (kategori.equals("Proses")) {
            for (Riwayat r : allRiwayatList) {
                if (r.getStatus().equalsIgnoreCase("Diproses")) {
                    filteredList.add(r);
                }
            }
        } else {
            // Filter berdasarkan Uang atau Barang
            for (Riwayat r : allRiwayatList) {
                if (r.getKategori().equalsIgnoreCase(kategori)) {
                    filteredList.add(r);
                }
            }
        }

        // Beritahu adapter bahwa datanya sudah diperbarui
        riwayatAdapter.updateData(filteredList);
    }

    // Fungsi animasi warna tab
    private void setActiveTab(MaterialButton activeTab) {
        MaterialButton[] allTabs = {binding.tabSemua, binding.tabUang, binding.tabBarang, binding.tabProses};
        for (MaterialButton tab : allTabs) {
            tab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4DFFFFFF")));
            tab.setTextColor(Color.WHITE);
        }
        activeTab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        activeTab.setTextColor(Color.parseColor("#333333"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}