package com.example.droidbarv1.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Producto;
import com.example.droidbarv1.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ComandaAdapter extends RecyclerView.Adapter<ComandaAdapter.MyViewHolder> {

    private LayoutInflater inflaterC;
    private List<Comanda> misComandas;
    private ComandaAdapter.OnItemClickListenner listener;
    private List<Producto> productos ;
    private Context context;

    public interface OnItemClickListenner{
        void onItemClick(Comanda comanda, View v);
    }

    public ComandaAdapter(ComandaAdapter.OnItemClickListenner listener, Context context) {
        this.listener=listener;
        inflaterC=LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ComandaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflaterC.inflate(R.layout.item_comanda,parent,false);
        MyViewHolder vh = new MyViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ComandaAdapter.MyViewHolder holder, int position) {
        String nombreP = "";
        int unidades;

        boolean encontrado = false;
        if(misComandas != null){
            final Comanda current = misComandas.get(position);
            for (Producto p:productos) {
                if(!encontrado&&current.getId_product() == p.getId()){
                    nombreP = p.getName();
                    encontrado = true;
                }
            }
            unidades = current.getUnits();
            holder.tvNombreProducto.setText(nombreP);
            holder.tvNumeroUnidades.setText(String.valueOf(unidades));
            if (current.isServed()==1){
                holder.ivServed.setVisibility(View.VISIBLE);
                holder.ivMas.setVisibility(View.INVISIBLE);
                holder.ivMenos.setVisibility(View.INVISIBLE);
            }else{
                holder.ivServed.setVisibility(View.INVISIBLE);
                holder.ivMas.setVisibility(View.VISIBLE);
                holder.ivMenos.setVisibility(View.VISIBLE);
            }

            holder.ivMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(current,v);
                }
            });
            holder.ivMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(current,v);
                }
            });
        }

    }
    public void setComandaList(List<Comanda>comandaList, List<Producto> productos){
        this.misComandas=comandaList;
        if(productos!=null){
            this.productos=productos;
        }
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        misComandas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        int elementos=0;
        if(misComandas!=null){
            elementos=misComandas.size();
        }
        return elementos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvProducto, tvNombreProducto, tvUnidades, tvNumeroUnidades;
        ImageView ivMas, ivMenos,ivServed;
        public CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvUnidades = itemView.findViewById(R.id.tvUnidades);
            tvNumeroUnidades = itemView.findViewById(R.id.tvNumeroUnidades);
            ivMas = itemView.findViewById(R.id.ivMas);
            ivMenos = itemView.findViewById(R.id.ivMenos);
            ivServed= itemView.findViewById(R.id.ivServed);
            cardView=itemView.findViewById(R.id.productCard);
        }
    }
}
