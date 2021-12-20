package com.luisrosero.workermaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.luisrosero.workermaps.adaptadores.AdapTrabajadores;
import com.luisrosero.workermaps.entidades.Trabajador;

public class TrabajadoresAct extends AppCompatActivity {

    private ImageButton imgAtras;
    private RecyclerView rcvTrabajadores;
    private AdapTrabajadores adaptador;
    private FloatingActionButton fabAdicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajadores);


        setWidgets();
        llenarRecycler();
        funBotones();
    }

    private void funBotones() {

        fabAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrabajadoresAct.this, NewWorkerAct.class));
            }
        });

        imgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void llenarRecycler() {

        rcvTrabajadores.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rcvTrabajadores.setLayoutManager(llm);

        Query query = FirebaseFirestore.getInstance()
                .collection("trabajadores")
                .limit(50);

        FirestoreRecyclerOptions<Trabajador> options = new FirestoreRecyclerOptions.Builder<Trabajador>()
                .setQuery(query, Trabajador.class)
                .build();

        adaptador = new AdapTrabajadores(options);

        rcvTrabajadores.setAdapter(adaptador);
        adaptador.startListening();





    }

    private void setWidgets() {
        rcvTrabajadores = findViewById(R.id.rcv_trabajadores);
        fabAdicionar = findViewById(R.id.fab_adicionar);
        imgAtras = findViewById(R.id.imb_atras);
    }
}