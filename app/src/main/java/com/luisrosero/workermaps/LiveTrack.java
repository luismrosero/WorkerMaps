package com.luisrosero.workermaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;
import com.luisrosero.workermaps.entidades.Activo;
import com.luisrosero.workermaps.entidades.Trabajador;

import static android.content.Context.LOCATION_SERVICE;

public class LiveTrack {

    private static final String ID = "IDM";
    private static final double DISTANCIA = 0.07; // a cuadra y media
    private double latitud = 0.0;
    private double longitud = 0.0;
    private LocationManager locationManager;
    private LocationListener listener;
    private DatabaseReference mDatabase;
    private boolean sigue = true;
    private Context context;
    private Trabajador vehiculo;

    public LiveTrack(Context context, Trabajador vehiculo) {
        this.context = context;
        this.vehiculo = vehiculo;

        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    public void getLocalizacion() {

        Log.e("esta ", "get location");

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                if(isDiferente(location)){
                    Log.e("IS", "diferente");
                    subirDatos(location, vehiculo);

                }else{
                    Log.e("NO", "diferente");

                }

                latitud = location.getLatitude();
                longitud = location.getLongitude();
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.e("provedor", s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.e("provedor", s);
                Toast.makeText(context, "Error en Gps.", Toast.LENGTH_SHORT).show();
                // Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                // startActivity(i);



            }
        };

        locationManager.requestLocationUpdates("gps", 5000, 0, listener);


    }

    public  void subirDatos(Location location, Trabajador vehiculo) {

        GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
        Activo activo = new Activo(vehiculo, geoPoint);

        mDatabase.child("activos").child(vehiculo.getId()).setValue(activo);

    }

    private boolean isDiferente(Location location) {


        double distanciaActual = coordGpsToKm( latitud, longitud,location.getLatitude(), location.getLongitude());
        Log.e("Distancia", String.valueOf(distanciaActual));
        return distanciaActual > DISTANCIA;

    }

    public static double coordGpsToKm(double lat1, double lon1, double lat2, double lon2) {
        double lat1rad = Math.toRadians(lat1);
        double lon1rad = Math.toRadians(lon1);
        double lat2rad = Math.toRadians(lat2);
        double lon2rad = Math.toRadians(lon2);

        double difLatitud = lat1rad - lat2rad;
        double difLongitud = lon1rad - lon2rad;

        double a = Math.pow(Math.sin(difLatitud / 2), 2) +
                Math.cos(lat1rad) *
                        Math.cos(lat2rad) *
                        Math.pow(Math.sin(difLongitud / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double radioTierraKm = 6378.0;
        double distancia = radioTierraKm * c;

        return distancia;
    }

    public void desactivar(){

        mDatabase.child("activos").child(vehiculo.getId()).removeValue();
        locationManager.removeUpdates(listener);
    }

}
