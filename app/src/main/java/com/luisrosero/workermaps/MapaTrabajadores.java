package com.luisrosero.workermaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luisrosero.workermaps.entidades.Activo;
import com.luisrosero.workermaps.entidades.Trabajador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MapaTrabajadores extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CardView crvMas;
    private FloatingActionButton fabInicio;
    private ManejoServicio manejoServicio;
    private ArrayList<Activo> activos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_trabajadores);


        manejoServicio = new ManejoServicio();
        estWidgets();
        funBotones();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manejoServicio.isServicioActivo(fabInicio);
        
        
        
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v, Gravity.BOTTOM);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_inicio, popup.getMenu());
        popup.setGravity(0);
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.trabajadores:
                         startActivity(new Intent(MapaTrabajadores.this, TrabajadoresAct.class));
                        break;

              

                    case R.id.perfil:
                        //  startActivity(new Intent(ActInicio.this, ActTerminosLegales.class));
                        break;
                }

                return true;
            }
        });
    }

    private void funBotones() {

        crvMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        fabInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activar();
            }
        });



    }

    private void activar() {

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        String subID = email.replaceAll("@","_");
        String id = subID.replaceAll("\\.","-").toLowerCase();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trabajadores").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Trabajador trabajador = task.getResult().toObject(Trabajador.class);
                    if (manejoServicio.isServicioActivo(fabInicio)){
                        stopService(new Intent(MapaTrabajadores.this, ServicioEscucha.class));
                        manejoServicio.isServicioActivo(fabInicio);

                        manejoServicio.retirarVheiculo(trabajador);

                    }else{
                        startService(new Intent(MapaTrabajadores.this, ServicioEscucha.class));
                        manejoServicio.isServicioActivo(fabInicio);
                    }
                }
            }
        });

    }


    private void estWidgets() {
        crvMas = findViewById(R.id.crv_mas_inicio);
        fabInicio = findViewById(R.id.fab_activar);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0.8249167, -77.6294601);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 11));
        
        verificarSuperVisor();
        
    }

    private void verificarSuperVisor() {
        Trabajador trabajador =  new BDLocal(this).getTrabajador();
        
        if (trabajador.isSupervisor()){
            funVerActivos();
        }
    }

    private void funVerActivos() {


        DatabaseReference mDatabase;
        String email = "none";


        mDatabase = FirebaseDatabase.getInstance().getReference().child("activos");


        String finalEmail = email;
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                activos = new ArrayList<>();
                mMap.clear();
               // encotrarUbicacionSolo();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Activo activo = ds.getValue(Activo.class);
                    activos.add(activo);

                    LatLng latLng = new LatLng(activo.getLati(), activo.getLongi());

                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(activo.getNombre()));



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("Cambio", "No");
            }
        };
        mDatabase.addValueEventListener(postListener);




    }
}