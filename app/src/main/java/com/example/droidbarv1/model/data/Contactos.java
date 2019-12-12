package com.example.droidbarv1.model.data;

public class Contactos {
    private String nombre, email;

    public Contactos(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public Contactos() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}