package id.dicoding.mirel.uangkost;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import id.dicoding.mirel.uangkost.database.DataHelper;
import id.dicoding.mirel.uangkost.report.ReportActivity;



public class KategoriPendapatan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Initialize Variable
     */
    String[] DaftarPendapatan, IdPendapatan;
    ListView ListViewPendapatan;
    protected Cursor cursor;
    DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kategori_pendapatan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initial ListView
        ListViewPendapatan = findViewById(R.id.listViewPendapatan);

        dataHelper = new DataHelper(this);

        new MyAsynch().execute();
    }

    /**
     * start asyntask onCreate and Data Changed
     */
    private class MyAsynch extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... Strings) { // run time intensive task in separate thread
            GetData();
            return null;
        }

        protected void onPostExecute(String result) {
            // Give the data to you adapter from here,instead of the place where you gave it earlier
            LoadListView();
        }
    }

    /**
     * Load Data From Database Category
     */
    public void GetData (){
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM kategori_pendapatan", null);
        // Initial Array Size
        DaftarPendapatan = new String[cursor.getCount()];
        IdPendapatan     = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            // Get Id
            IdPendapatan[cc] = cursor.getString(0).toString();
            // Get Item Name
            DaftarPendapatan[cc] = cursor.getString(1).toString();
        }
    }

    /**
     * Load Data From Database & Set Into ListView
     */
    public void LoadListView() {
        // membuat sebuah adapter yang berfungsi untuk menampung data sementara sebelum di tampilkan ke dalam list view
        MyAdapter adapter = new MyAdapter(this, DaftarPendapatan);
        //menampilkan / memasukan adapter ke dalam ListView
        ListViewPendapatan.setAdapter(adapter);
        // Set On ClickListener on each Item
        ListViewPendapatan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get item clicked ID
                final String idn = IdPendapatan[position];
                // Get item clicked Name
                final String nama = DaftarPendapatan[position];
                // Set Option when item clicked
                final CharSequence[] dialogitem = {"Edit Kategori"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KategoriPendapatan.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                // User clicked the Update Option
                                showEditDialog(idn, nama);
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        ((ArrayAdapter) ListViewPendapatan.getAdapter()).notifyDataSetInvalidated();
    }

    /**
     * Adapter for Set Data ListView as CardView
     */
    private class MyAdapter extends ArrayAdapter {
        String list_nama[];
        Activity activity;

        //konstruktor
        public MyAdapter(KategoriPendapatan kategoriPendapatan, String[] list_nama) {
            super(kategoriPendapatan, R.layout.item_kategori, list_nama);
            activity = kategoriPendapatan;
            this.list_nama = list_nama;
        }

        //menthode yang digunakan untuk memanggil layout listview dan mengenalkan widgetnya
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // panggil layout item_kategori_pengeluaran
            LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();
            View v = inflater.inflate(R.layout.item_kategori, null);
            // kenalkan widget yang ada pada item_kategori_pengeluaran
            TextView nama;
            TextView kategori;
            ImageView imageView;

            //casting widget id
            kategori = v.findViewById(R.id.namakategori);
            imageView = v.findViewById(R.id.list_avatar);
            nama = v.findViewById(R.id.deskrip);

            // set data kedalam cardview
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.add_circular_32));
            nama.setText(list_nama[position]);
            kategori.setText("Pendapatan");

            return v;
        }
    }

    /**
     * Form Dialog for Update Category
     */
    public void showEditDialog(final String idParam, final String namaParam) {
        // Initial Layout to Inflate the dialogbox
        LayoutInflater mainLayout = LayoutInflater.from(KategoriPendapatan.this);
        View mView = mainLayout.inflate(R.layout.dialog_edit_kategori, null);
        final EditText editCategory = mView.findViewById(R.id.userInputDialog);
        editCategory.setText(namaParam);
        // Build the Dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(KategoriPendapatan.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                // User Click the Save Button
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Empty Field
                        if (editCategory.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Nama Kategori Harus Diisi", Toast.LENGTH_LONG).show();
                        }
                        // Not Empty Field
                        else {
                            SQLiteDatabase dbRead = dataHelper.getReadableDatabase();
                            cursor = dbRead.rawQuery("SELECT * FROM kategori_pendapatan WHERE jenis = '" + editCategory.getText().toString() + "'", null);
                            int a = cursor.getCount();
                            // Name Already Used
                            if (a > 0){
                                Toast.makeText(getApplicationContext(), "Kategori " + namaParam + " Sudah Ada !", Toast.LENGTH_LONG).show();
                            }
                            // Do Update
                            else {
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("UPDATE kategori_pendapatan SET jenis = '"+ editCategory.getText().toString() + "' WHERE id ='" + idParam + "'");
                                new MyAsynch().execute();
                                Toast.makeText(getApplicationContext(), "Edit " + namaParam + " Menjadi " + editCategory.getText().toString() + " Berhasil", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                })
                // User Click the Cancel Button
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        // Show the Dialog
        alertDialogAndroid.show();
    }

    /**
     * Form Dialog for New Category
     */
    public void showNewCategoryDialog(View v) {
        // Initial Layout to Inflate the dialogbox
        LayoutInflater mainLayout = LayoutInflater.from(KategoriPendapatan.this);
        View mView = mainLayout.inflate(R.layout.dialog_tambah_kategori, null);
        final EditText newCategory = mView.findViewById(R.id.userInputDialog);
        // Build the Dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(KategoriPendapatan.this);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput
                .setCancelable(false)
                // User Click the Save Button
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Empty Field
                        if (newCategory.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Nama Kategori Harus Diisi", Toast.LENGTH_LONG).show();
                        }
                        // Not Empty Field
                        else {
                            SQLiteDatabase dbRead = dataHelper.getReadableDatabase();
                            cursor = dbRead.rawQuery("SELECT * FROM kategori_pendapatan WHERE jenis = '" + newCategory.getText().toString() + "'", null);
                            int a = cursor.getCount();
                            // Name Already Used
                            if (a > 0){
                                Toast.makeText(getApplicationContext(), "Kategori " + newCategory.getText().toString() + " Sudah Ada !", Toast.LENGTH_LONG).show();
                            }
                            // Do Insert
                            else {
                                SQLiteDatabase dbWrite = dataHelper.getWritableDatabase();
                                dbWrite.execSQL("INSERT INTO 'kategori_pendapatan' ('jenis') VALUES ('"+ newCategory.getText().toString() +"') ");
                                new MyAsynch().execute();
                                Toast.makeText(getApplicationContext(), "Berhasil Menambah Kategori " + newCategory.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                })
                // User Click the Cancel Button
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        // Show the Dialog
        alertDialogAndroid.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(KategoriPendapatan.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dompet) {
            Intent intent = new Intent(KategoriPendapatan.this, DompetActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_kategori_pendapatan) {
            Toast.makeText(this, "Kategori Pendapatan", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_kategori_pengeluaran) {
            Intent intent = new Intent(KategoriPendapatan.this, KategoriPengeluaran.class);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(KategoriPendapatan.this, ReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            showAboutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Show About Dialog
     */
    private void showAboutDialog() {
        View mView = getLayoutInflater().inflate(R.layout.dialog_about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_foreground);
        builder.setTitle(R.string.app_name);
        builder.setView(mView);
        builder.create();
        TextView textView = mView.findViewById(R.id.about_link_credits);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView textView2 = mView.findViewById(R.id.about_link_credits2);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());
        TextView textView3 = mView.findViewById(R.id.about_link_credits3);
        textView3.setMovementMethod(LinkMovementMethod.getInstance());
        builder.show();
    }
}