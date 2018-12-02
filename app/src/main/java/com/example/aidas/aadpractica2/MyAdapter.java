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

    private ArrayList<Contacto> contactos;
    private OnItemClickListener listener;

    public MyAdapter(ArrayList<Contacto> contactos, OnItemClickListener listener) {

        this.contactos = contactos;
        this.listener = listener;

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

        myViewHolder.bind(contactos.get(i), listener, i);

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

        }

        public void bind(final Contacto contacto, final OnItemClickListener listener, final int i) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(contacto, i);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Contacto contacto, int i);
    }
}
