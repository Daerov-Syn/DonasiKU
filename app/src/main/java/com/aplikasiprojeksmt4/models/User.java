package com.aplikasiprojeksmt4.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String nama;
    private String email;
    private String profile_photo;
    private String no_telepon;
    private String alamat;
    private String role;
    private boolean emailVerified;

    public User() {
        // Required for Firestore
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfile_photo() { return profile_photo; }
    public void setProfile_photo(String profile_photo) { this.profile_photo = profile_photo; }

    public String getNo_telepon() { return no_telepon; }
    public void setNo_telepon(String no_telepon) { this.no_telepon = no_telepon; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}
