package com.aplikasiprojeksmt4.ui;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class NotifikasiPageFragment extends Fragment {

    private FragmentNotifkasipageBinding binding;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotifkasipageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        // Mock data
        notificationList.add(new Notification("1", getString(R.string.notif_donasi_tersalurkan), 
                getString(R.string.notif_donasi_tersalurkan_desc), getString(R.string.notif_time_50m), 
                android.R.drawable.checkbox_on_background, Color.parseColor("#E8F5E9"), Color.parseColor("#4CAF50")));
        
        notificationList.add(new Notification("2", getString(R.string.notif_penjemputan_dijadwalkan), 
                getString(R.string.notif_donasi_tersalurkan_desc), getString(R.string.notif_time_2h), 
                android.R.drawable.ic_menu_myplaces, Color.parseColor("#FFF3E0"), Color.parseColor("#FF9800")));
        
        notificationList.add(new Notification("3", getString(R.string.notif_kampanye_hampir_selesai), 
                getString(R.string.notif_kampanye_desc), getString(R.string.notif_time_1d), 
                android.R.drawable.ic_menu_recent_history, Color.parseColor("#FFEBEE"), Color.parseColor("#F44336")));
        
        notificationList.add(new Notification("4", getString(R.string.notif_laporan_mingguan), 
                getString(R.string.notif_laporan_desc), getString(R.string.notif_time_1w), 
                android.R.drawable.ic_menu_sort_by_size, Color.parseColor("#F3E5F5"), Color.parseColor("#9C27B0")));
        
        notificationList.add(new Notification("5", getString(R.string.notif_sertifikat_baru), 
                getString(R.string.notif_sertifikat_desc), getString(R.string.notif_time_2w), 
                android.R.drawable.btn_star_big_on, Color.parseColor("#FFF9C4"), Color.parseColor("#FBC02D")));

        adapter = new NotificationAdapter(notificationList);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(adapter);

        // Add swipe to dismiss functionality
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

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvNotifications);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
