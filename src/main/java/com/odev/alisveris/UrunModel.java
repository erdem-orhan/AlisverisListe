package com.odev.alisveris;

public class UrunModel {
    private String urunadi;
    private String urunadeti;
    private String urunfiyati;

    public void setUrunadi(String urunadi) {
        this.urunadi = urunadi;
    }

    public String getUrunadeti() {
        return urunadeti;
    }

    public void setUrunadeti(String urunadeti) {
        this.urunadeti = urunadeti;
    }

    public String getUrunfiyati() {
        return urunfiyati;
    }

    public void setUrunfiyati(String urunfiyati) {
        this.urunfiyati = urunfiyati;
    }

    public UrunModel(String urunadi, String urunadeti, String urunfiyati) {
        this.urunadi = urunadi;
        this.urunadeti = urunadeti;
        this.urunfiyati = urunfiyati;
    }

    public String getUrunadi() {
        return urunadi;
    }
}
