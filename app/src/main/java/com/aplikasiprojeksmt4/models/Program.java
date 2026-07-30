package com.aplikasiprojeksmt4.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class Program implements Serializable {
    private String id;
    private String nama; // Judul Program
    private String organisasi;
    private String wilayah;
    private String tipe; // Kategori: Dana / Barang
    private String target; // Target Dana atau Barang (misal "100 Paket")
    private String batas_waktu; 
    private String deskripsi;
    private String penerima_manfaat;
    private String rencana_penggunaan;
    private String nama_pic;
    private String no_whatsapp;
    private String imageUrl;
    private String status; // Default: "Menunggu Review"
    private long terkumpul;
    private String dibuat_oleh;
    private String dibuat_oleh_nama; // Nama Mitra/Organisasi
    private int donatur_count;
    private int penerima_count;
    private long siap_tarik;
    
    @ServerTimestamp
    private Date created_at;

    public Program() {
        // Required for Firebase
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getOrganisasi() { return organisasi; }
    public void setOrganisasi(String organisasi) { this.organisasi = organisasi; }

    public String getWilayah() { return wilayah; }
    public void setWilayah(String wilayah) { this.wilayah = wilayah; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getBatas_waktu() { return batas_waktu; }
    public void setBatas_waktu(String batas_waktu) { this.batas_waktu = batas_waktu; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getPenerima_manfaat() { return penerima_manfaat; }
    public void setPenerima_manfaat(String penerima_manfaat) { this.penerima_manfaat = penerima_manfaat; }

    public String getRencana_penggunaan() { return rencana_penggunaan; }
    public void setRencana_penggunaan(String rencana_penggunaan) { this.rencana_penggunaan = rencana_penggunaan; }

    public String getNama_pic() { return nama_pic; }
    public void setNama_pic(String nama_pic) { this.nama_pic = nama_pic; }

    public String getNo_whatsapp() { return no_whatsapp; }
    public void setNo_whatsapp(String no_whatsapp) { this.no_whatsapp = no_whatsapp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTerkumpul() { return terkumpul; }
    public void setTerkumpul(long terkumpul) { this.terkumpul = terkumpul; }

    public String getDibuat_oleh() { return dibuat_oleh; }
    public void setDibuat_oleh(String dibuat_oleh) { this.dibuat_oleh = dibuat_oleh; }

    public String getDibuat_oleh_nama() { return dibuat_oleh_nama; }
    public void setDibuat_oleh_nama(String dibuat_oleh_nama) { this.dibuat_oleh_nama = dibuat_oleh_nama; }

    public int getDonatur_count() { return donatur_count; }
    public void setDonatur_count(int donatur_count) { this.donatur_count = donatur_count; }

    public int getPenerima_count() { return penerima_count; }
    public void setPenerima_count(int penerima_count) { this.penerima_count = penerima_count; }

    public long getSiap_tarik() { return siap_tarik; }
    public void setSiap_tarik(long siap_tarik) { this.siap_tarik = siap_tarik; }

    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }
    
    public long getTargetValue() {
        if (target == null) return 0;
        String clean = target.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(clean);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getTargetUnit() {
        if (target == null) return "";
        return target.replaceAll("[0-9]", "").trim();
    }
}
