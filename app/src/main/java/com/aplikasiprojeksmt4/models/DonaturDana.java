package com.aplikasiprojeksmt4.models;

import com.google.firebase.Timestamp;
import java.util.Date;

public class DonaturDana {
    private String id;
    private String userId;
    private String namaDonatur;
    private String tanggalDonasi;
    private long nominal;
    private String pesan;
    private String fotoBarang; 
    private String metodePengiriman; 
    private String status;
    private String programId;
    private String programNama; // Added for easier history display
    private int jumlahTransaksi;
    private int jumlahKategori;

    private Object timestamp;

    public DonaturDana() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getNamaDonatur() { return namaDonatur; }
    public void setNamaDonatur(String namaDonatur) { this.namaDonatur = namaDonatur; }
    public String getTanggalDonasi() { return tanggalDonasi; }
    public void setTanggalDonasi(String tanggalDonasi) { this.tanggalDonasi = tanggalDonasi; }
    public long getNominal() { return nominal; }
    public void setNominal(long nominal) { this.nominal = nominal; }
    public String getPesan() { return pesan; }
    public void setPesan(String pesan) { this.pesan = pesan; }
    public String getFotoBarang() { return fotoBarang; }
    public void setFotoBarang(String fotoBarang) { this.fotoBarang = fotoBarang; }
    public String getMetodePengiriman() { return metodePengiriman; }
    public void setMetodePengiriman(String metodePengiriman) { this.metodePengiriman = metodePengiriman; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public String getProgramNama() { return programNama; }
    public void setProgramNama(String programNama) { this.programNama = programNama; }

    public int getJumlahTransaksi() {
        return jumlahTransaksi;
    }

    public int getJumlahKategori() {
        return jumlahKategori;
    }

    public void setJumlahTransaksi(int jumlahTransaksi) {
        this.jumlahTransaksi = jumlahTransaksi;
    }

    public void setJumlahKategori(int jumlahKategori) {
        this.jumlahKategori = jumlahKategori;
    }

    public Date getTimestamp() {
        if (timestamp instanceof Timestamp) {
            return ((Timestamp) timestamp).toDate();
        } else if (timestamp instanceof Date) {
            return (Date) timestamp;
        } else if (timestamp instanceof String) {
            try {
                // Mencoba parsing jika formatnya standar ISO atau sejenisnya
                // Jika tidak bisa, kembalikan null atau handle sesuai kebutuhan
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).parse((String) timestamp);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
}
