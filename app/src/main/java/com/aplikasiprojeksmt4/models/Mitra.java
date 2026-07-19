package com.aplikasiprojeksmt4.models;

import java.util.Map;

public class Mitra {
    private String id;
    private String nama;
    private String email;
    private String alamat;
    private String fotoUrl;
    private String nomorRekening;
    private String namaBank;
    private Map<String, String> dokumen; // Map of document name to URL

    public Mitra() {
        // Required for Firestore
    }

    public Mitra(String id, String nama, String email, String alamat, String fotoUrl, String nomorRekening, String namaBank, Map<String, String> dokumen) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.alamat = alamat;
        this.fotoUrl = fotoUrl;
        this.nomorRekening = nomorRekening;
        this.namaBank = namaBank;
        this.dokumen = dokumen;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getNomorRekening() { return nomorRekening; }
    public void setNomorRekening(String nomorRekening) { this.nomorRekening = nomorRekening; }

    public String getNamaBank() { return namaBank; }
    public void setNamaBank(String namaBank) { this.namaBank = namaBank; }

    public Map<String, String> getDokumen() { return dokumen; }
    public void setDokumen(Map<String, String> dokumen) { this.dokumen = dokumen; }
}
