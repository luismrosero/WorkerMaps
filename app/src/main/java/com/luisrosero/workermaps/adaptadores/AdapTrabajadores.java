package com.luisrosero.workermaps.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.luisrosero.workermaps.R;
import com.luisrosero.workermaps.entidades.Trabajador;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class AdapTrabajadores extends FirestoreRecyclerAdapter<Trabajador, AdapTrabajadores.TrabajadorViewHolder> {


    public AdapTrabajadores(@NonNull @NotNull FirestoreRecyclerOptions<Trabajador> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull AdapTrabajadores.TrabajadorViewHolder holder,
                                    int position, @NonNull @NotNull Trabajador trabajador) {

        holder.txtNombre.setText(trabajador.getNombre());
        holder.txtCelular.setText(trabajador.getCelular());
        if (trabajador.isSupervisor()){
            holder.txtTipo.setText("Supervisor");
        }

        Picasso.get().load(trabajador.getImg()).into(holder.imgFoto);

    }

    @NonNull
    @NotNull
    @Override
    public TrabajadorViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trabajador, parent, false);
        return new TrabajadorViewHolder(v);
    }

    class TrabajadorViewHolder extends RecyclerView.ViewHolder {


        TextView txtNombre, txtTipo, txtCelular;
        ImageView imgFoto;

        public TrabajadorViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txt_nombre);
            txtCelular = itemView.findViewById(R.id.txt_celular);
            txtTipo = itemView.findViewById(R.id.txt_tipo);
            imgFoto = itemView.findViewById(R.id.img_foto);
        }
    }
}
