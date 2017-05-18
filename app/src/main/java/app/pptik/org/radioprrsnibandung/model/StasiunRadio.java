package app.pptik.org.radioprrsnibandung.model;

import java.io.Serializable;

/**
 * Created by Hafid on 5/16/2017.
 */

public class StasiunRadio implements Serializable{
    private int id;
    private int id_broadcast;
    private String nama;
    private String alamat;
    private String broadcast_path;
    private String frekuensi;
    private String band;
    private String site_addr;
    private String about;
    private String prof_pic_uri;
    private boolean info;

    public StasiunRadio(){}

    public StasiunRadio(int id, String nama){
        this.id = id;
        this.nama = nama;
    }

    public StasiunRadio(int id, String nama, String alamat){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_broadcast() {
        return id_broadcast;
    }

    public void setId_broadcast(int id_broadcast) {
        this.id_broadcast = id_broadcast;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getBroadcast_path() {
        return broadcast_path;
    }

    public void setBroadcast_path(String broadcast_path) {
        this.broadcast_path = broadcast_path;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isInfo() {
        return info;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }

    public String getProf_pic_uri() {
        return prof_pic_uri;
    }

    public void setProf_pic_uri(String prof_pic_uri) {
        this.prof_pic_uri = prof_pic_uri;
    }

    public String getFrekuensi() {
        return frekuensi;
    }

    public void setFrekuensi(String frekuensi) {
        this.frekuensi = frekuensi;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getSite_addr() {
        return site_addr;
    }

    public void setSite_addr(String site_addr) {
        this.site_addr = site_addr;
    }

    @Override
    public String toString() {
        return "Entity : "+this.getNama()+", "+this.getBroadcast_path();
    }
}
