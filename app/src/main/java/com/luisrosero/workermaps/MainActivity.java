package com.luisrosero.workermaps;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luisrosero.workermaps.entidades.Trabajador;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private static final String ID = "IDM";

    private Button btnEntrar;
    private TextInputEditText edtUsuario, edtPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWidgets();
        funBotones();

        NotificationChannel channel = new NotificationChannel(ID, "noti", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        if (new ManejoServicio().isServicioActivo(this)){
            startActivity(new Intent(MainActivity.this, MapaTrabajadores.class));
        }

      //  startActivity(new Intent(MainActivity.this, TrabajadoresAct.class));

    }


    private void funBotones() {

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarEdts()){
                    logueoComerciante();
                }
            }
        });


    }

    private void logueoComerciante() {

        String email = edtUsuario.getText().toString();
        String pass = edtPass.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String subID = email.replaceAll("@","_");
                    String id = subID.replaceAll("\\.","-").toLowerCase();

                    db.collection("trabajadores").document(id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){

                                        if (task.getResult().exists()){
                                            Trabajador trabajador = task.getResult().toObject(Trabajador.class);
                                            new BDLocal(MainActivity.this).setTrabajador(trabajador);
                                            Intent intent = new Intent(MainActivity.this, MapaTrabajadores.class);
                                            startActivity(intent);
                                        }



                                    }
                                }
                            });


                }else{


                    Toast.makeText(MainActivity.this, "Error en Usuario o Contraseña", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    private boolean verificarEdts() {

        if (edtUsuario.getText().toString().isEmpty() || edtPass.getText().toString().isEmpty()) {

            if (edtPass.getText().toString().isEmpty()){
                edtPass.setError("campo obligatorio");
            }

            if (edtUsuario.getText().toString().isEmpty()){
                edtUsuario.setError("campo obligatorio");
            }

            return false;
        } else {
            return true;
        }

    }

    private void setWidgets() {

        edtUsuario = findViewById(R.id.tie_usuario);
        edtPass = findViewById(R.id.tie_pass);
        btnEntrar = findViewById(R.id.btn_entrar);

    }
}