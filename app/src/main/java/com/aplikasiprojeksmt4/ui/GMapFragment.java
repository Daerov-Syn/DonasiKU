package com.aplikasiprojeksmt4.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aplikasiprojeksmt4.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class GMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        db = FirebaseFirestore.getInstance();

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false);

            if ((fineLocationGranted != null && fineLocationGranted) ||
                    (coarseLocationGranted != null && coarseLocationGranted)) {
                enableMyLocation();
            } else {
                Toast.makeText(requireContext(), "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gmap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Memanggil map dengan cara yang aman
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();

        // Fokuskan kamera ke Surabaya sebagai default (Zoom level 11)
        LatLng pusatSurabaya = new LatLng(-7.250445, 112.768845);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pusatSurabaya, 11f));

        // Memuat titik lokasi dari Firebase
        loadMarkersFromFirebase();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void enableMyLocation() {
        if (mMap == null) return;
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // Jangan animate camera lagi ke lokasi user agar view awal tetap ke Surabaya sesuai UI
                            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void loadMarkersFromFirebase() {
        // Asumsi data titik penyaluran ada di koleksi "titik_penyaluran" (bisa diubah sesuai struktur database Anda)
        db.collection("titik_penyaluran")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Ambil data latitude dan longitude. Pastikan field di Firebase bertipe Number/Double
                            Double lat = document.getDouble("latitude");
                            Double lng = document.getDouble("longitude");
                            String namaProgram = document.getString("nama_program");

                            if (lat != null && lng != null) {
                                LatLng point = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(namaProgram != null ? namaProgram : "Titik Penyaluran"));
                            }
                        }
                    } else {
                        // Jika tidak ada data dari Firebase, tampilkan data dummy di Bulak Banteng (Sesuai Gambar Figma)
                        LatLng bulakBanteng = new LatLng(-7.2144, 112.7761);
                        mMap.addMarker(new MarkerOptions().position(bulakBanteng).title("Santunan Lansia Bulak Banteng"));
                    }
                });
    }
}