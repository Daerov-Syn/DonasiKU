package com.aplikasiprojeksmt4.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;

public class Withdrawal implements Serializable {
    private String id;
    private String programId;
    private long nominal;
    private String tanggal;
    private String metode;
    private String status;
    private Object timestamp;

    public Withdrawal() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public long getNominal() { return nominal; }
    public void setNominal(long nominal) { this.nominal = nominal; }
    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public String getMetode() { return metode; }
    public void setMetode(String metode) { this.metode = metode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTimestamp() {
        if (timestamp instanceof Timestamp) return ((Timestamp) timestamp).toDate();
        if (timestamp instanceof Date) return (Date) timestamp;
        return null;
    }
    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
}
