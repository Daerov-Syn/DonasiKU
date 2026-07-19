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

public class StatistikFragment extends Fragment {

    private PageStatistikBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageStatistikBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup WebView for Map (OpenStreetMap Surabaya)
        setupMapWebView();

        // Setup Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.nav_statistik);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) {
                Navigation.findNavController(view).navigate(R.id.action_StatistikFragment_to_HomepageAdminFragment);
                return true;
            } else if (id == R.id.nav_statistik) {
                return true;
            }
            return false;
        });
    }

    private void setupMapWebView() {
        WebView webView = binding.webviewMap;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // URL OpenStreetMap focused on Surabaya with markers (Leaflet via a simple static map or public view)
        // For simplicity, we use the public OSM share URL for Surabaya area.
        String surabayaUrl = "https://www.openstreetmap.org/export/embed.html?bbox=112.55, -7.35, 112.85, -7.15&layer=mapnik&marker=-7.2575,112.7521";
        webView.loadUrl(surabayaUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
