package com.aplikasiprojeksmt4.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ItemMitraNotificationBinding;
import com.aplikasiprojeksmt4.models.Notification;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MitraNotificationAdapter extends RecyclerView.Adapter<MitraNotificationAdapter.ViewHolder> {

    private List<Notification> notifications;

    public MitraNotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMitraNotificationBinding binding = ItemMitraNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMitraNotificationBinding binding;

        public ViewHolder(ItemMitraNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notification notification) {
            binding.tvTitle.setText(notification.getTitle());
            binding.tvDescription.setText(notification.getDescription());
            binding.tvTime.setText(notification.getTime());
            binding.ivIcon.setImageResource(notification.getIconResId());
            binding.cardIcon.setCardBackgroundColor(notification.getIconBgColor());
            binding.ivIcon.setColorFilter(notification.getIconTint());

            binding.tvAction.setOnClickListener(v -> {
                String programId = notification.getProgramId();
                if (programId != null) {
                    FirebaseFirestore.getInstance().collection("programs").document(programId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    Program p = doc.toObject(Program.class);
                                    if (p != null) {
                                        p.setId(doc.getId());
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("program", p);
                                        Navigation.findNavController(v).navigate(R.id.action_ManajemenProgramFragment_to_DetailDonasiMitraFragment, bundle);
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}
