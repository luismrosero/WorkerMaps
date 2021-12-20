package com.luisrosero.workermaps.entidades;

public class Trabajador {

    private String id;
    private String nombre;
    private String celular;
    private boolean supervisor;
    private String correo;
    private String pass;
    private String img;

    public Trabajador() {
    }

    public Trabajador(String nombre, String celular, boolean supervisor, String correo, String pass, String img) {
        String preId = correo.replaceAll("@","_" );
        this.id = preId.replaceAll("\\.","-").toLowerCase();
        this.nombre = nombre;
        this.celular = celular;
        this.supervisor = supervisor;
        this.correo = correo;
        this.pass = pass;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public boolean isSupervisor() {
        return supervisor;
    }

    public void setSupervisor(boolean supervisor) {
        this.supervisor = supervisor;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
