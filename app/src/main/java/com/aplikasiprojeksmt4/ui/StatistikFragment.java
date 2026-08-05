package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.PageStatistikBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Locale;

public class StatistikFragment extends Fragment {

    private PageStatistikBinding binding;
    private FirebaseFirestore db;
    private ListenerRegistration danaListener;
    private ListenerRegistration barangListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageStatistikBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMapWebView();
        loadRealtimeStats();

        binding.bottomNavigation.setSelectedItemId(R.id.nav_statistik);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) {
                Navigation.findNavController(view).navigate(R.id.action_StatistikFragment_to_HomepageAdminFragment);
                return true;
            } else if (id == R.id.nav_statistik) {
                return true;
            } else if (id == R.id.nav_program_admin) {
                Navigation.findNavController(view).navigate(R.id.action_StatistikFragment_to_PageProgramFragment);
                return true;
            } else if (id == R.id.nav_donatur) {
                Navigation.findNavController(view).navigate(R.id.action_StatistikFragment_to_DonaturAdminFragment);
                return true;
            }
            return false;
        });
    }

    private void loadRealtimeStats() {
        danaListener = db.collection("donatur_dana").addSnapshotListener((danaDocs, e1) -> {
            if (binding == null || !isAdded()) return;
            barangListener = db.collection("donatur_barang").addSnapshotListener((barangDocs, e2) -> {
                if (binding == null || !isAdded()) return;

                long totalDana = 0;
                long totalBarang = 0;
                long countDanaTx = 0;
                long countBarangTx = 0;

                if (danaDocs != null) {
                    countDanaTx = danaDocs.size();
                    for (QueryDocumentSnapshot doc : danaDocs) {
                        Long nominal = doc.getLong("nominal");
                        if (nominal != null) totalDana += nominal;
                    }
                }

                if (barangDocs != null) {
                    countBarangTx = barangDocs.size();
                    for (QueryDocumentSnapshot doc : barangDocs) {
                        if ("Diverifikasi".equals(doc.getString("status"))) {
                            totalBarang++;
                        }
                    }
                }

                // Update UI Summary
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                nf.setMaximumFractionDigits(0);
                binding.tvStatTotalDana.setText(nf.format(totalDana));
                binding.tvStatTotalBarang.setText(String.valueOf(totalBarang));
                
                // Assuming some default or logic for these
                binding.tvStatTotalPenerima.setText("40"); 

                // Calculate Percentages
                long totalTx = countDanaTx + countBarangTx;
                if (totalTx > 0) {
                    float pBarang = (countBarangTx * 100f) / totalTx;
                    float pUang = (countDanaTx * 100f) / totalTx;
                    
                    binding.tvPercentBarang.setText(Math.round(pBarang) + "%");
                    binding.tvPercentUang.setText(Math.round(pUang) + "%");
                    binding.pieChart.setPercentages(pBarang, pUang);
                }
            });
        });
    }

    private void setupMapWebView() {
        WebView webView = binding.webviewMap;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String surabayaUrl = "https://www.openstreetmap.org/export/embed.html?bbox=112.55, -7.35, 112.85, -7.15&layer=mapnik&marker=-7.2575,112.7521";
        webView.loadUrl(surabayaUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (danaListener != null) danaListener.remove();
        if (barangListener != null) barangListener.remove();
        binding = null;
    }
}
