package com.example.aidas.aadpractica2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditarContacto extends AppCompatActivity {

    private static final int CODIGO_EDITAR_CONTACTO = 1;
    private TextView tvDTNombre;
    private EditText tvDNombre;
    private TextView tvTTelefono;
    private EditText tvDTelefono;
    private Button btGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cotacto);
        this.btGuardar = (Button) findViewById(R.id.btGuardar);
        this.tvDTelefono = (EditText) findViewById(R.id.tvDTelefono);
        this.tvTTelefono = (TextView) findViewById(R.id.tvTTelefono);
        this.tvDNombre = (EditText) findViewById(R.id.tvDNombre);
        this.tvDTNombre = (TextView) findViewById(R.id.tvDTNombre);

        Contacto contacto = getIntent().getParcelableExtra("contacto");

        if (contacto != null) {

            this.tvDNombre.setText(contacto.getNombre());
            this.tvDTelefono.setText(contacto.getTelefono());


        } else {

            setResult(EditarContacto.RESULT_CANCELED);

            finish();

        }

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = tvDNombre.getText().toString();
                String telefono = tvDTelefono.getText().toString();

                Contacto contacto = new Contacto(nombre, telefono);

                Intent i = new Intent();

                i.putExtra("contactoNuevo", contacto);

                setResult(EditarContacto.RESULT_OK, i);

                finish();

            }
        });

    }
}
