package com.example.aidas.aadpractica2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar tb;
    private RecyclerView mRV;
    private RecyclerView.LayoutManager mLM;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Contacto> contactos = new ArrayList<>();
    private ArrayList<Contacto> contactosGuardados = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 55;
    private static final String TAG = "MITAG";

    private Toolbar toolbar;
    private android.support.design.widget.AppBarLayout appbar;
    private android.widget.Button btContactos;
    private RecyclerView rvContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.rvContactos = (RecyclerView) findViewById(R.id.rvContactos);
        this.btContactos = (Button) findViewById(R.id.btContactos);
        this.appbar = (AppBarLayout) findViewById(R.id.appbar);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        tb = findViewById(R.id.toolbar);
        tb.setTitle(R.string.tituloA);

        setListeners();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putArrayList
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarRV();
    }

    private void setListeners(){

        btContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(MainActivity.this, "You have already granted this permission!",
                            Toast.LENGTH_SHORT).show();

                    getContactos();
                    guardarEnMemoriaInternaP();

                    mAdapter.notifyDataSetChanged();

                }else{

                    requestReadContactsPermission();
                }

            }
        });
    }

    private void muestraExplicacion() {

    }

    public void requestReadContactsPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_CONTACTS)) {

            //Esto debe hacerse de forma asíncrona.
            //muestraExplicacion();

            new AlertDialog.Builder(this)
                    .setTitle("Es necesario un permiso.")
                    .setMessage("Este permiso es necesario para acceder a tus contactos.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    })
                    .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }else{

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(TAG, "Permisos concedidos.");
                    Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();

                    getContactos();

                    guardarEnMemoriaInternaP();
                    leerMemoriaInternaP();

                    /*
                    if(isExternalStorageWritable()){

                        guardarEnMemoriaExternaP();
                        leerMemoriaExternaP();

                    }else{
                        Log.v(TAG, "No se puede escribir en la memoria externa");
                    }
                    */

                    cargarRV();

                } else {

                    Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                }

                return;
            }

        default:
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getContactos(){

        //Creamosun contentResolver y un cursor para poder recorrer todos los contactos.

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        //Mientras el cursor no está vacío lo recorremos.

        if ((cur != null ? cur.getCount() : 0) > 0) {

            String nombre = "";
            String telefono = "";

            while (cur != null && cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                nombre = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //Comprobamos si el contacto tiene asociados numeros de telf, de ser así
                //con un cursor obtenemos solo el primero.

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor telfCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    telfCur.moveToFirst();

                    telefono = telfCur.getString(telfCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Log.v(TAG, "Name: " + nombre);
                    Log.v(TAG, "Phone Number: " + telefono);

                    telfCur.close();
                }

                //Por último vamos guardando la información obtenida en un ArrayList de objetos Contacto
                Contacto c =  new Contacto(nombre, telefono);
                contactos.add(c);

                Log.v(TAG, c.getNombre() + " añadido.");
            }
        } else {
            Log.v(TAG, "Cursor vacío en getContactos");
        }

        //Cerramos el cursor.
        if(cur != null){

            cur.close();

        }
    }

    public void cargarRV(){

        mRV = (RecyclerView) findViewById(R.id.rvContactos);

        mAdapter = new MyAdapter(contactosGuardados);
        mRV.setAdapter(mAdapter);

        mLM = new LinearLayoutManager(this);
        mRV.setLayoutManager(mLM);

    }

    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;

    }

    public String setCSVString(){

        String fileContents = "";

        if(contactos!=null && !contactos.isEmpty()){

            for(Contacto c: contactos){

                fileContents += "\'" + c.getNombre() + "\';\'" + c.getTelefono() + "\'\n";
            }

        }

        return fileContents;
    }

    public void guardarEnMemoriaInternaP(){

        String filename = "contactos.txt";
        String fileContents = setCSVString();
        FileOutputStream outputStream;

        try {

            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void leerMemoriaInternaP(){

        Log.v(TAG, "Leer memoria interna");

        String nombre;
        String telefono;
        String filename = "contactos.txt";
        File file = new File(MainActivity.this.getFilesDir(), filename);

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null){

                String[] datos = st.split(";");

                nombre = datos[0].substring(1,datos[0].length() - 1);

                telefono = datos[1].substring(1, datos[1].length() - 1);

                Contacto c = new Contacto(nombre, telefono);

                contactosGuardados.add(c);

                Log.v(TAG, "Contacto " + c.getNombre() + " añadido.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarEnMemoriaExternaP() {

        String filename = "contactos.txt";
        String fileContents = setCSVString();
        FileOutputStream outputStream;

        //Pasamos null para que acceda a la memoria privada de nuestra aplicación.
        File file = new File(MainActivity.this.getExternalFilesDir(null), filename);

        try {

            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();

            Log.v(TAG, "despuesde escribir en MExternaPrivada");

        } catch (Exception e) {

            e.printStackTrace();

        }

        Log.v(TAG, "path del file: " + file.getPath());

    }

    public void leerMemoriaExternaP(){

        String filename = "contactos.txt";
        File file = new File(MainActivity.this.getExternalFilesDir(null), filename);

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null){
                Log.v(TAG,"-----------------------------------------------------");
                Log.v(TAG, "Linea: " + st);
            }

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

}
