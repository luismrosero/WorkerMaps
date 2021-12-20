package com.luisrosero.workermaps.entidades;

import com.google.firebase.firestore.GeoPoint;

public class Activo {


    private double longi;
    private double lati;
    private String nombre;


    public Activo() {
    }

    public Activo(Trabajador vehiculo, GeoPoint location) {

        this.longi = location.getLongitude();
        this.lati = location.getLatitude();
        this.nombre = vehiculo.getNombre();

    }


    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
