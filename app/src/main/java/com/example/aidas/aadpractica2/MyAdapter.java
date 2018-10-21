package com.example.aidas.aadpractica2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{

    ArrayList<Contacto> contactos;

    public MyAdapter(ArrayList<Contacto> contactos) {

        this.contactos = contactos;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row,
                viewGroup, false);

        return (new MyViewHolder(itemView));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.nombre.setText(contactos.get(i).getNombre());

        myViewHolder.telefono.setText(contactos.get(i).getTelefono());

        Log.d("longitud bind", String.valueOf(i));
    }

    @Override
    public int getItemCount() {

        Log.d("longitud count", String.valueOf(contactos.size()));

        return contactos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nombre;
        TextView telefono;

        public MyViewHolder(@NonNull View itemView){

            super(itemView);

            //Asociamos las istas del viewHolder con las correspondientes en nuestro layout.

            nombre = itemView.findViewById(R.id.tvNombre);

            telefono = itemView.findViewById(R.id.tvTelefono);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Llama a actividad editcontacto
                }
            });

        }
    }

}
