package com.example.droidbarv1.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.droidbarv1.activities.EmailActivity;
import com.example.droidbarv1.model.data.Empleado;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.activities.PrintTicketActivity;
import com.example.droidbarv1.R;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.AdapterViewHolder> {
    private List<Factura> facturas;
    private List<Empleado> empleados;
    //LISTA DE EMPLEADOS
    private Context context;
    private String nomEmpleado="";

    public HistorialAdapter(Context context) {
        this.context = context;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView factura;
        public TextView mesa;
        public TextView empleado;
        public TextView fCierre;
        public CardView cardView;
        public ImageView ivPrint;
        public ImageView ivEmail;

        public AdapterViewHolder(View v) {
            super(v);
            factura = v.findViewById(R.id.tvNFact);
            mesa = v.findViewById(R.id.tvMesa);
            empleado = v.findViewById(R.id.tvECierre);
            fCierre = v.findViewById(R.id.tvHCierre);
            cardView=v.findViewById(R.id.history_card);
            ivPrint=v.findViewById(R.id.ivPrint);
            ivEmail = v.findViewById(R.id.ivEmail);

        }
    }

    public void setFacturaEmpleadoList(List<Factura>facturas,List<Empleado>empleados){
        this.facturas=facturas;
        this.empleados=empleados;
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        facturas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        int elementos=0;
        if(facturas!=null){
            elementos=facturas.size();
        }
        return elementos;
    }


    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_historial, viewGroup, false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder viewHolder, int i) {
        final int position=i;

        for (Empleado e:empleados
             ) {
            if(e.getId()==facturas.get(i).getId_employee_finish()){
                nomEmpleado=e.getLogin();
            }
        }
        viewHolder.factura.setText(String.valueOf(facturas.get(i).getId()));
        viewHolder.mesa.setText("Mesa: "+facturas.get(i).getTable());
        viewHolder.empleado.setText("Empleado: "+nomEmpleado);
        viewHolder.fCierre.setText("H.Cierre: "+facturas.get(i).getFinish_time());
        viewHolder.ivPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent printIntent = new Intent(context, PrintTicketActivity.class);
                printIntent.putExtra("finF",facturas.get(position));
                context.startActivity(printIntent);
            }
        });
        viewHolder.ivEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEmail = new Intent(context, EmailActivity.class);
                intentEmail.putExtra("facturaF", facturas.get(position));
                intentEmail.putExtra("nomEmpleado",nomEmpleado);
                context.startActivity(intentEmail);
            }
        });

    }
}
