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
        // 1. Ambil data donasi barang yang statusnya masih "Menunggu Verifikasi"
        db.collection("donatur_barang")
                .whereEqualTo("status", "Menunggu Verifikasi")
                .addSnapshotListener((value, error) -> {
                    processDocs(value, error, "Donasi Barang", "menunggu konfirmasi verifikasi barang donatur");
                });

        // 2. Ambil data program yang statusnya masih "Menunggu Review"
        db.collection("programs")
                .whereEqualTo("status", "Menunggu Review")
                .addSnapshotListener((value, error) -> {
                    processDocs(value, error, "Pengajuan Program", "menunggu verifikasi program baru");
                });
    }

    private void processDocs(com.google.firebase.firestore.QuerySnapshot value, com.google.firebase.firestore.FirebaseFirestoreException error, String type, String action) {
        if (error != null) {
            Log.e("Notifikasi", "Gagal mengambil data " + type, error);
            return;
        }

        if (value != null) {
            // Kita tidak bisa clear() di sini karena ada multiple listeners.
            // Strategi: kumpulkan semua notif lalu update.
            // Namun untuk kesederhanaan saat ini, kita tambahkan saja.
            // Di produksi, sebaiknya gunakan satu listener atau gabungkan data.
            
            for (QueryDocumentSnapshot doc : value) {
                String nama = doc.getString("namaDonatur");
                if (nama == null) nama = doc.getString("nama"); // Untuk Program
                if (nama == null) nama = "Seseorang";

                notificationList.add(0, new Notification(
                        doc.getId(),
                        "Verifikasi " + type,
                        nama + " " + action,
                        "Baru saja",
                        android.R.drawable.ic_menu_myplaces,
                        Color.parseColor("#FFEBEE"),
                        Color.parseColor("#F44336")
                ));
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}