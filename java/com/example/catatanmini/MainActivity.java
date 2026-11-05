package com.example.catatanmini;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View root;
    private TextView tvJudul, tvSapaan;
    private EditText etNama, etCatatan;
    private Button btnSimpanNama, btnTambah;
    private SwitchCompat swDarkMode;
    private ListView lvCatatan;

    private PrefsManager prefsManager;   // SharedPreferences: nama + mode gelap
    private DatabaseHelper db;           // SQLite: isi catatan

    private int warnaTeks = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        tvJudul = findViewById(R.id.tvJudul);
        tvSapaan = findViewById(R.id.tvSapaan);
        etNama = findViewById(R.id.etNama);
        etCatatan = findViewById(R.id.etCatatan);
        btnSimpanNama = findViewById(R.id.btnSimpanNama);
        btnTambah = findViewById(R.id.btnTambah);
        swDarkMode = findViewById(R.id.swDarkMode);
        lvCatatan = findViewById(R.id.lvCatatan);

        prefsManager = new PrefsManager(this);
        db = new DatabaseHelper(this);

        // Mode gelap disimpan langsung saat switch diubah
        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.saveDarkMode(isChecked);
            terapkanTema(isChecked);
        });

        // Nama disimpan ke SharedPreferences
        btnSimpanNama.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            prefsManager.saveNama(nama);
            tampilkanSapaan(nama);
            Toast.makeText(this, "Nama disimpan", Toast.LENGTH_SHORT).show();
        });

        // Catatan disimpan ke SQLite
        btnTambah.setOnClickListener(v -> {
            String isi = etCatatan.getText().toString().trim();
            if (isi.isEmpty()) {
                Toast.makeText(this, "Catatan masih kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            String tanggal = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"))
                    .format(new Date());
            db.tambah(tanggal, isi);
            etCatatan.setText("");
            muatCatatan();
            Toast.makeText(this, "Catatan ditambahkan", Toast.LENGTH_SHORT).show();
        });

        // Tekan lama sebuah catatan untuk menghapusnya
        lvCatatan.setOnItemLongClickListener((parent, view, position, id) -> {
            DatabaseHelper.Catatan c = (DatabaseHelper.Catatan) parent.getItemAtPosition(position);
            db.hapus(c.id);
            muatCatatan();
            Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    // Pengaturan dibaca ulang setiap layar tampil (dibuka, atau kembali dari background)
    @Override
    protected void onResume() {
        super.onResume();
        String nama = prefsManager.getNama();
        boolean gelap = prefsManager.isDarkMode();

        etNama.setText(nama);
        swDarkMode.setChecked(gelap);
        tampilkanSapaan(nama);
        terapkanTema(gelap);
    }

    private void tampilkanSapaan(String nama) {
        if (nama.isEmpty()) {
            tvSapaan.setText("Halo! Isi namamu dulu ya.");
        } else {
            tvSapaan.setText("Halo, " + nama + "! Apa kegiatanmu hari ini?");
        }
    }

    private void terapkanTema(boolean gelap) {
        int latar = gelap ? Color.parseColor("#212121") : Color.parseColor("#FFFFFF");
        warnaTeks = gelap ? Color.WHITE : Color.BLACK;

        root.setBackgroundColor(latar);
        tvJudul.setTextColor(warnaTeks);
        tvSapaan.setTextColor(warnaTeks);
        etNama.setTextColor(warnaTeks);
        etNama.setHintTextColor(Color.GRAY);
        etCatatan.setTextColor(warnaTeks);
        etCatatan.setHintTextColor(Color.GRAY);
        swDarkMode.setTextColor(warnaTeks);

        muatCatatan(); // daftar dimuat ulang supaya warna teksnya ikut berubah
    }

    // Ambil semua catatan dari SQLite lalu tampilkan di ListView
    private void muatCatatan() {
        List<DatabaseHelper.Catatan> data = db.semua();
        ArrayAdapter<DatabaseHelper.Catatan> adapter = new ArrayAdapter<DatabaseHelper.Catatan>(
                this, android.R.layout.simple_list_item_1, data) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(warnaTeks);
                return tv;
            }
        };
        lvCatatan.setAdapter(adapter);
    }
}