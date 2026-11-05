package com.example.catatanmini;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * DatabaseHelper: menyimpan isi catatan harian di SQLite.
 * Tabel "catatan" berisi: id, tanggal, isi.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "catatan_harian.db";
    private static final int DB_VERSION = 1;
    private static final String TABEL = "catatan";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABEL + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tanggal TEXT, "
                + "isi TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABEL);
        onCreate(db);
    }

    // Tambah satu catatan baru
    public void tambah(String tanggal, String isi) {
        ContentValues nilai = new ContentValues();
        nilai.put("tanggal", tanggal);
        nilai.put("isi", isi);
        getWritableDatabase().insert(TABEL, null, nilai);
    }

    // Ambil semua catatan, yang terbaru di atas
    public ArrayList<Catatan> semua() {
        ArrayList<Catatan> daftar = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABEL, null, null, null, null, null, "id DESC");
        while (c.moveToNext()) {
            Catatan cat = new Catatan();
            cat.id = c.getInt(c.getColumnIndexOrThrow("id"));
            cat.tanggal = c.getString(c.getColumnIndexOrThrow("tanggal"));
            cat.isi = c.getString(c.getColumnIndexOrThrow("isi"));
            daftar.add(cat);
        }
        c.close();
        return daftar;
    }

    // Hapus catatan berdasarkan id
    public void hapus(int id) {
        getWritableDatabase().delete(TABEL, "id=?", new String[]{String.valueOf(id)});
    }

    // Model data satu catatan
    public static class Catatan {
        public int id;
        public String tanggal;
        public String isi;

        @Override
        public String toString() {
            return tanggal + "\n" + isi;
        }
    }
}