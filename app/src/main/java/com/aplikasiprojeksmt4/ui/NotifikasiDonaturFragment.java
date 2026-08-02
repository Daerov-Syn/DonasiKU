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

import com.aplikasiprojeksmt4.databinding.FragmentNotifkasipageBinding;
import com.aplikasiprojeksmt4.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotifikasiDonaturFragment extends Fragment {

    private FragmentNotifkasipageBinding binding;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotifkasipageBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        fetchNotifikasiDonatur();
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        binding.rvNotifikasi.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifikasi.setAdapter(adapter);

        // Fitur Swipe to Dismiss (opsional, sesuaikan dengan kebutuhan)
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.removeNotification(position);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvNotifikasi);
    }

    private void fetchNotifikasiDonatur() {
        db.collection("notifikasi") // Arahkan ke koleksi notifikasi donatur
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("NotifikasiDonatur", "Gagal mengambil data", error);
                        return;
                    }

                    if (value != null) {
                        notificationList.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            String judul = doc.getString("judul");
                            String pesan = doc.getString("pesan");
                            String waktu = doc.getString("waktu");
                            String tipeIkon = doc.getString("tipe_ikon");

                            judul = (judul != null) ? judul : "Pemberitahuan";
                            pesan = (pesan != null) ? pesan : "Pesan baru";
                            waktu = (waktu != null) ? waktu : "Baru saja";
                            tipeIkon = (tipeIkon != null) ? tipeIkon : "info";

                            int iconRes = android.R.drawable.ic_dialog_info;
                            int bgColor = Color.parseColor("#F3E5F5");
                            int iconColor = Color.parseColor("#9C27B0");

                            if (tipeIkon.equalsIgnoreCase("sukses") || judul.toLowerCase().contains("tersalurkan")) {
                                iconRes = android.R.drawable.checkbox_on_background;
                                bgColor = Color.parseColor("#E8F5E9");
                                iconColor = Color.parseColor("#4CAF50");
                            } else if (judul.toLowerCase().contains("penjemputan") || judul.toLowerCase().contains("barang")) {
                                iconRes = android.R.drawable.ic_menu_gallery;
                                bgColor = Color.parseColor("#FFF3E0");
                                iconColor = Color.parseColor("#FF9800");
                            }

                            notificationList.add(new Notification(doc.getId(), judul, pesan, waktu, iconRes, bgColor, iconColor));
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