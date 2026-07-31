package com.aplikasiprojeksmt4.models;

public class Riwayat {
    private String judul;
    private String tanggal;
    private String status;
    private String nominal;
    private String kategori;

    public Riwayat(String judul, String tanggal, String status, String nominal, String kategori) {
        this.judul = judul;
        this.tanggal = tanggal;
        this.status = status;
        this.nominal = nominal;
        this.kategori = kategori;
    }

    public String getJudul() { return judul; }
    public String getTanggal() { return tanggal; }
    public String getStatus() { return status; }
    public String getNominal() { return nominal; }
    public String getKategori() { return kategori; }
}