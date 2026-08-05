package com.aplikasiprojeksmt4.models;

import com.google.firebase.Timestamp;
import java.util.Date;

public class DonaturBarang {
    private String id;
    private String userId; // FK ke users
    private String namaDonatur; // Bisa "Anonim"
    private String tanggalDonasi;
    private String kondisi; // baru/sangat baik/baik
    private String deskripsi;
    private String fotoBarang;
    private String metodePengiriman;
    private String status;
    private String programId; // FK ke programs
    private Object timestamp;

    public DonaturBarang() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getNamaDonatur() { return namaDonatur; }
    public void setNamaDonatur(String namaDonatur) { this.namaDonatur = namaDonatur; }
    public String getTanggalDonasi() { return tanggalDonasi; }
    public void setTanggalDonasi(String tanggalDonasi) { this.tanggalDonasi = tanggalDonasi; }
    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getFotoBarang() { return fotoBarang; }
    public void setFotoBarang(String fotoBarang) { this.fotoBarang = fotoBarang; }
    public String getMetodePengiriman() { return metodePengiriman; }
    public void setMetodePengiriman(String metodePengiriman) { this.metodePengiriman = metodePengiriman; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public Date getTimestamp() {
        if (timestamp instanceof Timestamp) {
            return ((Timestamp) timestamp).toDate();
        } else if (timestamp instanceof Date) {
            return (Date) timestamp;
        } else if (timestamp instanceof String) {
            try {
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).parse((String) timestamp);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
}
