package com.luisrosero.workermaps;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.luisrosero.workermaps.entidades.Activo;
import com.luisrosero.workermaps.entidades.Trabajador;


public class ServicioEscucha extends Service {
    private static final String ID = "IDM";
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference mDatabase;

    public ServicioEscucha() {


    }



    @Override
    public void onCreate() {

        Log.e("Creo","SERVICIO ESCUCHA");

        mDatabase = FirebaseDatabase.getInstance().getReference();


    }


    public  void subirDatos(double latitud, double longitud) {

        Trabajador trabajador = new BDLocal(getApplicationContext()).getTrabajador();
        GeoPoint geoPoint = new GeoPoint(latitud,longitud);
        Activo activo = new Activo(trabajador, geoPoint);

        mDatabase.child("activos").child(trabajador.getId()).setValue(activo);

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                obtenerUbicacion();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDatabase.child("registro").child("V1").addValueEventListener(listener);

        // Looper.prepare();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ID);
        builder.setContentTitle("Trabajador Activo");
        //  builder.setContentText("a");
        builder.setDefaults(Notification.DEFAULT_SOUND);

        startForeground(145, builder.build());
        // Looper.loop();
        return START_STICKY;


    }

    private void obtenerUbicacion() {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getCurrentLocation(100, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull

            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()){
                    double latit = task.getResult().getLatitude();
                    double longi = task.getResult().getLongitude();
                    subirDatos(latit, longi);
                }

            }
        });


    }

    @Override
    public void onDestroy() {
        Log.e("Finaliza", "ok");
        super.onDestroy();
    }
}
