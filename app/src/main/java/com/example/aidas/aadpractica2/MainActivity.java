package com.example.aidas.aadpractica2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MITAG";

    Toolbar tb;

    private RecyclerView mRV;
    private RecyclerView.LayoutManager mLM;
    private RecyclerView.Adapter mAdapter;

    private static ArrayList<Contacto> contactosTelefono = new ArrayList<>();
    private ArrayList<Contacto> contactosGuardados = new ArrayList<>();

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 55;
    private static final int CODIGO_EDITAR_CONTACTO = 1;
    private static final String CODIGO_INTERNA = "interna";
    private static final String CODIGO_EXTERNA = "externa";

    private static String CODIGO_MEMORIA;

    private Contacto contactoDetalle = new Contacto();
    private int posicionContacoSelec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mRV = (RecyclerView) findViewById(R.id.rvContactos);

        tb = findViewById(R.id.toolbar);
        tb.setTitle(R.string.tituloA);
        setSupportActionBar(tb);

        if(savedInstanceState != null){

            Log.v(TAG, "savedInstanceState");

            contactosGuardados = savedInstanceState.getParcelableArrayList("contactos");

            if(!contactosGuardados.isEmpty()){

                cargarRV();

                Log.v(TAG, "savedInstanceState - contactos");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("contactos", contactosGuardados);

        Log.d(TAG, "onSaveInstanteState");

    }

    @Override
    protected void onResume() {
        super.onResume();

        //cargarRV();
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

                    Log.v(TAG, "CODIGO: " + CODIGO_MEMORIA);

                    if(CODIGO_MEMORIA.equalsIgnoreCase(CODIGO_INTERNA)){

                        guardarEnMemoriaInternaP(setCSVString(contactosTelefono));

                        leerMemoriaInternaP();

                        cargarRV();

                    }else if(CODIGO_MEMORIA.equalsIgnoreCase(CODIGO_EXTERNA)){

                        guardarEnMemoriaExternaP(setCSVString(contactosTelefono));

                        leerMemoriaExternaP();

                        cargarRV();
                    }

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

        //Instanciamos la variable que contiene/contendrá los contactos del teléfono.
        contactosTelefono = new ArrayList<>();

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

                    //Log.v(TAG, "Name: " + nombre);
                    //Log.v(TAG, "Phone Number: " + telefono);

                    telfCur.close();
                }

                //Por último vamos guardando la información obtenida en un ArrayList de objetos Contacto
                Contacto c =  new Contacto(nombre, telefono);
                contactosTelefono.add(c);

                //Log.v(TAG, c.getNombre() + " añadido.");
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

        mAdapter = new MyAdapter(contactosGuardados, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contacto contacto, int position) {

                contactoDetalle = contacto;

                Intent i = new Intent(MainActivity.this, EditarContacto.class);

                i.putExtra("contacto", contacto);

                startActivityForResult(i, CODIGO_EDITAR_CONTACTO);
            }
        });

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

    public String setCSVString(ArrayList<Contacto> contactos){

        String fileContents = "";

        if(contactos!=null && !contactos.isEmpty()){

            for(Contacto c: contactos){

                fileContents += "\'" + c.getNombre() + "\';\'" + c.getTelefono() + "\'\n";
            }

        }

        return fileContents;
    }

    public void guardarEnMemoriaInternaP(String fileContents){

        Log.v(TAG, "Guardar en MEMORIA INTERNA");

        String filename = "contactos.txt";
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

        String filename = "contactos.txt";
        File parent = MainActivity.this.getFilesDir();

        //Vaciamos la varible que contine los contactos guardados.
        contactosGuardados = new ArrayList<>();

        leerMemoria(parent, filename);

    }

    public void leerMemoria(File parent, String filename){

        String nombre;
        String telefono;
        File file = new File(parent, filename);

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null){

                String[] datos = st.split(";");

                nombre = datos[0].substring(1,datos[0].length() - 1);

                telefono = datos[1].substring(1, datos[1].length() - 1);

                Contacto c = new Contacto(nombre, telefono);

                contactosGuardados.add(c);

                //Log.v(TAG, "Contacto " + c.getNombre() + " añadido.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarEnMemoriaExternaP(String filecontents) {

        Log.v(TAG, "Guardar en memoria externa");

        try {
            File ruta_sd = MainActivity.this.getExternalFilesDir(null);

            File f = new File(ruta_sd.getAbsolutePath(), "contactos.txt");

            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));

            fout.write(filecontents);
            fout.close();

            Log.v(TAG, "File path: " + f.getAbsolutePath());

        } catch (Exception ex){
            Log.v(TAG, "Error al escribir fichero a tarjeta SD");
        }

    }

    public void leerMemoriaExternaP(){

        String filename = "contactos.txt";
        File parent = MainActivity.this.getExternalFilesDir(null);

        leerMemoria(parent, filename);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            //Guardar contactos en la memoria interna.
            case R.id.miInterna:

                //Controlamos dónde se van a almacenar los datos.
                CODIGO_MEMORIA = CODIGO_INTERNA;

                //Comprobamos si tenemos los permisos necesarios para leer los contactos
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(MainActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();

                    //Obtenemos los contactos del teléfono
                    getContactos();

                    //Los guardamos en la memoria
                    guardarEnMemoriaInternaP(setCSVString(contactosTelefono));

                    //Volvemos a obtener los contactos que hemos almacenado
                    leerMemoriaInternaP();

                    mAdapter.notifyDataSetChanged();


                }else{

                    requestReadContactsPermission();
                }

                return true;

            case R.id.miExterna:

                CODIGO_MEMORIA = CODIGO_EXTERNA;

                //Comprobamos si tenemos los permisos necesarios para leer los contactos
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(MainActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();

                    getContactos();
                    guardarEnMemoriaExternaP(setCSVString(contactosTelefono));
                    leerMemoriaExternaP();

                    mAdapter.notifyDataSetChanged();


                }else{

                    requestReadContactsPermission();
                }

                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODIGO_EDITAR_CONTACTO){

            //Obtenemos el contacto que ha sido editado.
            contactoDetalle = data.getParcelableExtra("contactoNuevo");

            //Lo actualizamos en el array
            contactosGuardados.remove(posicionContacoSelec);
            contactosGuardados.add(posicionContacoSelec, contactoDetalle);

            //Guardamos los nuevos datos.
            if(CODIGO_MEMORIA.equalsIgnoreCase(CODIGO_INTERNA)){

                guardarEnMemoriaInternaP(setCSVString(contactosGuardados));

                mAdapter.notifyDataSetChanged();

            }else if(CODIGO_MEMORIA.equalsIgnoreCase(CODIGO_EXTERNA)){

                guardarEnMemoriaExternaP(setCSVString(contactosGuardados));

                mAdapter.notifyDataSetChanged();
            }

        }
    }
}
