package com.luisrosero.workermaps;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luisrosero.workermaps.entidades.Trabajador;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class NewWorkerAct extends AppCompatActivity {


    private ImageButton imgAtras;
    private ImageView imgFoto;
    private TextInputEditText tipNombre, tipCelular, tipCorreo, tipPass;
    private Button btnCrear;
    private CheckBox chbSupervisor;

    private Uri uriImg;

    private final ActivityResultLauncher<CropImageContractOptions> cropImageC =
            registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageView.CropResult>() {
                @Override
                public void onActivityResult(CropImageView.CropResult result) {
                    Picasso.get().load(result.getUriContent()).into(imgFoto);
                    uriImg = result.getUriContent();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_worker);


        setWidgets();

        funBotones();

    }

    private void funBotones() {

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriImg == null){
                    Toast.makeText(NewWorkerAct.this, "Debes Subir una imagen del trabajador", Toast.LENGTH_SHORT).show();
                }else{
                    subirImgConductor();
                }

            }
        });

        imgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(true)
                        .setAspectRatio(4, 4)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setOutputCompressQuality(50);

                cropImageC.launch(options);
            }
        });
    }

    private void subirImgConductor() {

        Toast.makeText(this, "Subiendo...", Toast.LENGTH_SHORT).show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        UploadTask uploadTask = null;


        StorageReference ref = storageRef.child("conductores/" + Timestamp.now().getSeconds() + "C.jpg");
        uploadTask = ref.putFile(uriImg);


        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Task<Uri> taskUrl = ref.getDownloadUrl();
                    taskUrl.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@android.support.annotation.NonNull Task<Uri> task) {

                            String url = String.valueOf(task.getResult());
                            crearTrabajador(url);


                        }
                    });
                } else {
                    Toast.makeText(NewWorkerAct.this, "Error al guardar Imagen de Perfil, tome la imagen e intentelo nuevamente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void crearTrabajador(String url) {
        String nombre = tipNombre.getText().toString();
        String celular = tipCelular.getText().toString();
        boolean supervisor = chbSupervisor.isChecked();
        String correo = tipCorreo.getText().toString();
        String pass = tipPass.getText().toString();

        Trabajador trabajador = new Trabajador(nombre, celular, supervisor, correo, pass, url);


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("trabajadores").document(trabajador.getId()).set(trabajador)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(NewWorkerAct.this, "Trabajador Ingresdo con exito", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(NewWorkerAct.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void setWidgets() {

        imgAtras = findViewById(R.id.imb_atras);
        imgFoto = findViewById(R.id.img_foto);
        tipNombre = findViewById(R.id.tip_nombre);
        tipCelular = findViewById(R.id.tip_celular);
        chbSupervisor = findViewById(R.id.checkBox);
        tipCorreo = findViewById(R.id.tip_correo);
        tipPass = findViewById(R.id.tip_pass);
        btnCrear = findViewById(R.id.btn_crear);
    }
}