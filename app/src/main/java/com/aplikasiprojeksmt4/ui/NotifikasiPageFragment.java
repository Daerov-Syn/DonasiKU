package com.aplikasiprojeksmt4.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentNotifkasipageBinding;
import com.aplikasiprojeksmt4.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotifikasiPageFragment extends Fragment {

    private FragmentNotifkasipageBinding binding;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotifkasipageBinding.inflate(inflater, container, false);
        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();

        // Panggil data dari Firebase
        fetchRealtimeNotifications();
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();

        // Catatan: Jika di dalam NotificationAdapter Anda belum ada fitur "klik",
        // Anda mungkin perlu menyesuaikan adapter-nya nanti agar bisa pindah halaman.
        adapter = new NotificationAdapter(notificationList);
        binding.rvNotifikasi.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifikasi.setAdapter(adapter);

        // Fitur geser untuk menghapus (Swipe to dismiss) yang sudah Anda buat
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // Opsional: Anda juga bisa menambahkan perintah menghapus data di Firebase di sini nantinya
                adapter.removeNotification(position);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvNotifikasi);
    }

    private void fetchRealtimeNotifications() {
        // Contoh: Mengambil data donasi barang yang statusnya masih "Menunggu Verifikasi"
        // Sesuaikan nama koleksi ("donasi_barang") dan field ("status") dengan yang ada di database Anda
        db.collection("donasi_barang")
                .whereEqualTo("status", "Menunggu Verifikasi")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Notifikasi", "Gagal mengambil data", error);
                        return;
                    }

                    if (value != null) {
                        notificationList.clear(); // Bersihkan list sebelum diisi data baru

                        for (QueryDocumentSnapshot doc : value) {
                            // Mengambil data dari dokumen
                            String namaDonatur = doc.getString("namaDonatur") != null ? doc.getString("namaDonatur") : "Anonim";
                            String idBarang = doc.getId(); // Menyimpan ID untuk dilempar ke halaman detail

                            // Membuat objek Notification baru menggunakan data asli dari Firebase
                            // Pastikan parameter ini sesuai dengan urutan model Notification.java Anda
                            notificationList.add(new Notification(
                                    idBarang,
                                    "Verifikasi Donatur Barang",
                                    namaDonatur + " menunggu konfirmasi verifikasi barang donatur",
                                    "Baru saja", // Anda bisa mengubah ini dengan format waktu timestamp asli
                                    android.R.drawable.ic_menu_myplaces, // Ikon sementara
                                    Color.parseColor("#FFEBEE"), // Background icon
                                    Color.parseColor("#F44336")  // Warna icon
                            ));
                        }

                        // Beritahu adapter agar layar me-refresh
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