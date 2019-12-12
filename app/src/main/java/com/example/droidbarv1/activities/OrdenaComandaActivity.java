package com.example.droidbarv1.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.customview2.ValueSelector;
import com.example.droidbarv1.R;
import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.data.Producto;
import com.example.droidbarv1.model.contract.WaitResponseServer;
import com.example.droidbarv1.view.DroidBarViewModel;
import com.example.droidbarv1.view.adapters.ProductoAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class OrdenaComandaActivity extends AppCompatActivity {
    
    private long idFactura,idEmpleado;
    private Factura facturaActual;
    private List<Producto> productos;
    private ArrayList<Producto> productosFilter;
    private RecyclerView rvProductos;
    private DroidBarViewModel viewModel;
    private GridLayoutManager layoutManager;
    private ImageView btComida, btBebida;
    private ProductoAdapter adapter;
    private int destino = 0;
    private View fragment;
    private int cont =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordena_comanda);

        facturaActual=getIntent().getParcelableExtra("factura");
        idFactura = facturaActual.getId();
        idEmpleado = getIntent().getLongExtra("idEmpleado", 4);

        adapter = new ProductoAdapter(new ProductoAdapter.OnItemClickListenner() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(Producto producto, View v) {
                //Creamos comanda con producto pinchado
                cont++;
                System.out.println("Añado: "+cont);
                creaComanda(producto,1);
                Snackbar.make(v, producto.getName(), Snackbar.LENGTH_LONG).show();
            }

        }, new ProductoAdapter.OnLongItemClickListenner() {
            @Override
            public void onLongItemClick(final Producto producto, View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrdenaComandaActivity.this);
                final LayoutInflater layoutInflater = getLayoutInflater();
                View vistaAux = layoutInflater.inflate(R.layout.layout_alerta, null);
                builder.setView(vistaAux);
                final ValueSelector valueSelector= vistaAux.findViewById(R.id.valueSelector);
                valueSelector.setMinValue(0);
                valueSelector.setMaxValue(50);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int unidades = valueSelector.getValue();
                        cont++;
                        creaComanda(producto, unidades);
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }, this);

        viewModel =  ViewModelProviders.of(this).get(DroidBarViewModel.class);
        viewModel.getProductoList(new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                if(success){
                    productosFilter=new ArrayList<>();
                    productos=list;
                    for (Producto p:productos) {
                        if(p.getTarget()==destino){
                            productosFilter.add(p);
                        }
                    }
                    adapter.setProductoList(productosFilter);
                    adapter.setIcon(destino);
                    initComponents();
                }
            }
        });

    }


    private void creaComanda(Producto producto, int uds) {
        Comanda newC = new Comanda();
        newC.setUnits(uds);
        newC.setId_employee(idEmpleado);
        newC.setId_ticket(idFactura);
        newC.setId_product(producto.getId());
        newC.setPrice(producto.getPrice()*uds);
        newC.setServed(0);
        Log.v("product",newC.toString());
        addCommandServer(newC);

    }
    private void addCommandServer(final Comanda newC){
        viewModel.addComanda(newC, new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                if (success){
                    if(cont>0){
                        cont--;
                        System.out.println("Valor contador despues añade: "+cont);
                    }
                }else{
                    if(cont>0){
                        cont--;
                        System.out.println("error: ");
                    }
                }
            }
        });

    }


    private void initComponents() {

        rvProductos = findViewById(R.id.rvProductos);
        layoutManager = new GridLayoutManager(this, 3);
        rvProductos.setLayoutManager(layoutManager);
        rvProductos.setAdapter(adapter);

        btComida = findViewById(R.id.btComida);
        btComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destino = 1;
                productosFilter = new ArrayList<>();
                for (Producto p : productos
                ) {
                    if (p.getTarget() == destino) {
                        productosFilter.add(p);
                    }
                }
                adapter.setProductoList(productosFilter);
                adapter.setIcon(destino);
            }
        });

        btBebida = findViewById(R.id.btBebida);
        btBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destino = 0;
                productosFilter = new ArrayList<>();
                for (Producto p : productos
                ) {
                    if (p.getTarget() == destino) {
                        productosFilter.add(p);
                    }
                }
                adapter.setProductoList(productosFilter);
                adapter.setIcon(destino);
            }
        });
        fragment = findViewById(R.id.fragmentLoadingComanda);
        fragment.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        new LoadViewTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        new LoadViewTask().execute();
        return true;
    }
    private class LoadViewTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.setVisibility(View.VISIBLE);

        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(final Void... voids) {
            synchronized (this){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btBebida.setEnabled(false);
                        btComida.setEnabled(false);
                        rvProductos.setEnabled(false);
                    }
                });
                while(cont>0){
                }

            }
            return null;
        }
        protected void onPostExecute(Void result){
            fragment.setVisibility(View.INVISIBLE);
            finish();
        }
    }
}
