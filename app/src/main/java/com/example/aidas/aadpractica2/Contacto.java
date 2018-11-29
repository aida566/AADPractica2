package com.example.aidas.aadpractica2;

import android.os.Parcel;
import android.os.Parcelable;

public class Contacto implements Parcelable {

    private String nombre;
    private String telefono;

    public Contacto() {

        new Contacto(null, null);
    }

    public Contacto(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String setNombre(String nombre) {
        this.nombre = nombre;
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String setTelefono(String telefono) {
        this.telefono = telefono;
        return telefono;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(nombre);
        dest.writeString(telefono);

    }
}
