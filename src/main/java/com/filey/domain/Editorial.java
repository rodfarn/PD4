package com.filey.domain;

public class Editorial {
    private int id;
    private String nombre;
    private String contacto;

    public Editorial(String nombre, String contacto) {
        this(0, nombre, contacto);
    }

    public Editorial(int id, String nombre, String contacto) {
        this.id = id;
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la editorial es obligatorio.");
        }
        if (contacto == null || contacto.trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto de la editorial es obligatorio.");
        }
        this.nombre = nombre.trim();
        this.contacto = contacto.trim();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
}
