package com.example.aidas.aadpractica2;

public class Contacto {

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
}
