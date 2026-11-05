package com.example.catatanmini;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PrefsManager: pembungkus SharedPreferences.
 * Semua kode baca/tulis pengaturan ada di sini, jadi Activity tetap rapi.
 */
public class PrefsManager {

    private static final String PREF_NAME = "pengaturan_mini";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_DARK_MODE = "dark_mode";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Simpan nama pengguna (apply() = simpan di background, tidak memblokir UI)
    public void saveNama(String nama) {
        prefs.edit().putString(KEY_NAMA, nama).apply();
    }

    public String getNama() {
        return prefs.getString(KEY_NAMA, "");
    }

    // Simpan mode gelap (true = gelap, false = terang)
    public void saveDarkMode(boolean aktif) {
        prefs.edit().putBoolean(KEY_DARK_MODE, aktif).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}