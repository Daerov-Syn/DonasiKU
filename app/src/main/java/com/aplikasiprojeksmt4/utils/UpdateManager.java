package com.aplikasiprojeksmt4.utils;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.aplikasiprojeksmt4.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class UpdateManager {

    private static final String TAG = "UpdateManager";
    private static final String APK_NAME = "donasiku_update.apk";
    private final Context context;
    private final FirebaseFirestore db;

    public interface OnUpdateCheckListener {
        void onNoUpdate();
    }

    public UpdateManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public void checkForUpdates() {
        checkForUpdates(null);
    }

    public void checkForUpdates(OnUpdateCheckListener listener) {
        db.collection("app_settings").document("update_info")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String latestVersionName = documentSnapshot.getString("latest_version");
                        Long latestVersionCode = documentSnapshot.getLong("latest_version_code");
                        String downloadUrl = documentSnapshot.getString("download_url");
                        
                        long currentVersionCode = BuildConfig.VERSION_CODE;

                        if (latestVersionCode != null && latestVersionCode > currentVersionCode && downloadUrl != null) {
                            showUpdateDialog(latestVersionName != null ? latestVersionName : "Terbaru", downloadUrl);
                        } else if (listener != null) {
                            listener.onNoUpdate();
                        }
                    } else if (listener != null) {
                        listener.onNoUpdate();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking for updates", e);
                    if (listener != null) {
                        listener.onNoUpdate();
                    }
                });
    }

    private void showUpdateDialog(String version, String url) {
        new AlertDialog.Builder(context)
                .setTitle("Pembaruan DonasiKu Tersedia")
                .setMessage("Versi baru (" + version + ") telah tersedia.\n\nUnduh sekarang untuk mendapatkan fitur terbaru dan perbaikan sistem.")
                .setPositiveButton("Download & Install", (dialog, which) -> {
                    if (canInstallPackages()) {
                        startDownload(url);
                    } else {
                        requestInstallPermission();
                        // Simpan URL agar bisa didownload setelah izin diberikan (opsional, untuk UX lebih baik)
                        Toast.makeText(context, "Silakan coba lagi setelah memberikan izin.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Nanti", null)
                .setCancelable(false)
                .show();
    }

    private boolean canInstallPackages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    private void requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(context, "Aktifkan 'Izinkan dari sumber ini' untuk mengupdate aplikasi", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void startDownload(String url) {
        // Bersihkan file lama jika ada
        File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir == null) {
            downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
        File apkFile = new File(downloadsDir, APK_NAME);
        if (apkFile.exists()) {
            apkFile.delete();
        }

        Toast.makeText(context, "Mengunduh pembaruan...", Toast.LENGTH_LONG).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("DonasiKu Update");
        request.setDescription("Sedang mengunduh versi terbaru...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        
        // Gunakan setDestinationUri agar FileProvider bisa mengaksesnya dengan aman
        request.setDestinationUri(Uri.fromFile(apkFile));

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) return;
        
        final long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    installApk(apkFile);
                    context.unregisterReceiver(this);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    private void installApk(File file) {
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Gagal menginstal APK", e);
                Toast.makeText(context, "Gagal membuka file instalasi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "File APK tidak ditemukan");
            Toast.makeText(context, "File instalasi tidak ditemukan.", Toast.LENGTH_SHORT).show();
        }
    }
}
