package app.pptik.org.radioprrsnibandung.model;

import android.media.MediaPlayer;

import app.pptik.org.radioprrsnibandung.utility.DownloadRadioTask;

/**
 * Created by Hafid on 5/16/2017.
 */

public class Acara {
    private int id;
    private String nama;
    private String waktu;
    private String tanggal;
    private String radio_url;
    private String download_url;
    private String fileName;
    private DownloadRadioTask downloader;

    public Acara(){}

    public Acara(int id, String nama, String waktu, String tanggal, String radio_url) {
        this.id = id;
        this.nama = nama;
        this.waktu = waktu;
        this.tanggal = tanggal;
        this.radio_url = radio_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getRadio_url() {
        return radio_url;
    }

    public void setRadio_url(String radio_url) {
        this.radio_url = radio_url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public final DownloadRadioTask getDownloader() {
        return downloader;
    }

    public void setDownloader(DownloadRadioTask downloader) {
        this.downloader = downloader;
    }
}
