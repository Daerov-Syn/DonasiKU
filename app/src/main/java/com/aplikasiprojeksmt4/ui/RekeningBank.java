package com.aplikasiprojeksmt4.models;

public class RekeningBank {
    private String id;
    private String namaBank;
    private String nomorRekening;
    private String atasNama;
    private boolean isUtama;

    public RekeningBank(String id, String namaBank, String nomorRekening, String atasNama, boolean isUtama) {
        this.id = id;
        this.namaBank = namaBank;
        this.nomorRekening = nomorRekening;
        this.atasNama = atasNama;
        this.isUtama = isUtama;
    }

    public String getId() { return id; }
    public String getNamaBank() { return namaBank; }
    public String getNomorRekening() { return nomorRekening; }
    public String getAtasNama() { return atasNama; }
    public boolean isUtama() { return isUtama; }

    public void setUtama(boolean utama) { isUtama = utama; }
}