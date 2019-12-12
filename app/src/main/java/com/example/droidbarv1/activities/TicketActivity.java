package com.example.droidbarv1.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.droidbarv1.R;
import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.data.Producto;
import com.example.droidbarv1.model.contract.WaitResponseServer;
import com.example.droidbarv1.view.adapters.ComandaAdapter;
import com.example.droidbarv1.view.adapters.helpers.ComandaItemTouchHelper;
import com.example.droidbarv1.view.DroidBarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketActivity extends AppCompatActivity implements ComandaItemTouchHelper.RecyclerItemTouchHelperListener{
    private DroidBarViewModel viewModel;
    private int idMesa;
    private long idEmpleado;
    private Button btFinTicket;
    private TextView tvFactura,tvMesa,tvTotal;
    private float total=0;
    public static Factura facturaActual;
    public static List<Factura> facturas;
    public static List<Comanda> comandas;
    public static List<Comanda> comandasTicket;
    public static List<Producto> productos;
    private ComandaAdapter adapter;
    private Factura finF;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        facturaActual=getIntent().getParcelableExtra("factura");

        //Obtencion de datos de Intent o del Bundle
        if(savedInstanceState!=null){
            idMesa=savedInstanceState.getInt("idMesa");
            total=savedInstanceState.getFloat("total");

        }else {
            idMesa=facturaActual.getTable();
            idEmpleado=getIntent().getLongExtra("idEmpleado",0);
        }

        viewModel =  ViewModelProviders.of(this).get(DroidBarViewModel.class);
        getData(true);

    }

    private void getData(final Boolean filtro) {
        viewModel.getFacturaList(new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                if(success){
                    facturas=list;
                    System.out.println("Facturas "+facturas.toString());
                    for (Factura f:facturas) {
                        if(f.getTable()==facturaActual.getTable()&&f.getId_employee_finish()==4){
                            facturaActual=f;
                        }
                    }
                    tvFactura=findViewById(R.id.tvFactura);
                    tvFactura.setText("Factura Nº "+facturaActual.getId());
                    viewModel.getProductoList(new WaitResponseServer() {
                        @Override
                        public void waitingResponse(boolean success, List list) {
                            if(success){
                                productos=list;
                                viewModel.getComandaList(new WaitResponseServer() {
                                    @Override
                                    public void waitingResponse(boolean success, List list) {
                                        if(success){
                                            comandas=list;
                                            recuperaComandas();
                                            if(filtro){
                                               initComponents();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    private Boolean checkCommandServed(Comanda comanda){
        getCommands();
        for (Comanda c:comandasTicket) {
            if(c.getId_ticket()==comanda.getId_ticket()&&c.getId()==comanda.getId()){
                if(c.isServed()==1){
                    System.out.println("No puede BORRA");
                    return false;
                }
            }
        }
        return true;
    }
    private void getCommands(){
        viewModel.getComandaList(new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                if(success){
                    comandas=list;
                    recuperaComandas();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        idEmpleado=getIntent().getLongExtra("idEmpleado",0);
        getData(false);

    }

    private void recuperaComandas() {
        comandasTicket= new ArrayList<>();
        total=0;
        for (Comanda c:comandas
             ) {
            if(c.getId_ticket()==facturaActual.getId()){
                double priceProduct=0;
                for (Producto p:productos) {
                    if(p.getId()==c.getId_product()){
                        priceProduct=p.getPrice();
                    }
                }
                comandasTicket.add(c);
                total+=priceProduct*c.getUnits();
            }
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        tvTotal=findViewById(R.id.tvTotal);
        tvTotal.setText("Total: "+formatter.format(total)+"€");
        if (adapter!=null){
            adapter.setComandaList(comandasTicket,productos);
        }
    }


    private void initComponents() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicketActivity.this, OrdenaComandaActivity.class).putExtra("factura", facturaActual).putExtra("idEmpleado", idEmpleado));
            }
        });

        adapter = new ComandaAdapter(new ComandaAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(Comanda comanda, final View v) {
                v.setEnabled(false);
                if(checkCommandServed(comanda)){
                    if(v.getId()==findViewById(R.id.ivMenos).getId()){
                        //ivMenos restar comandas
                        if (comanda.getUnits() == 1){
                            //No se pueden quitar mas Poner sanck bar que diga desliza comanda hacia la izq para borrar.
                            Snackbar.make(v,R.string.noQuitar,Snackbar.LENGTH_LONG).show();
                            v.setEnabled(true);
                        }else {
                            comanda.setUnits(comanda.getUnits() - 1);
                            double priceProduct=0;
                            for (Producto p:productos) {
                                if(p.getId()==comanda.getId_product()){
                                    priceProduct=p.getPrice();
                                }
                            }
                            comanda.setPrice(comanda.getPrice()-priceProduct);
                            viewModel.updateComanda(comanda, new WaitResponseServer() {
                                @Override
                                public void waitingResponse(boolean success, List list) {
                                    if(success){
                                        getCommands();
                                        v.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }else{
                        //ivMas añadir unidades a comanda
                        comanda.setUnits(comanda.getUnits() + 1);
                        double priceProduct=0;
                        for (Producto p:productos) {
                            if(p.getId()==comanda.getId_product()){
                                priceProduct=p.getPrice();
                            }
                        }
                        comanda.setPrice(comanda.getPrice()+priceProduct);
                        viewModel.updateComanda(comanda, new WaitResponseServer() {
                            @Override
                            public void waitingResponse(boolean success, List list) {
                                if(success){
                                    getCommands();
                                    v.setEnabled(true);
                                }
                            }
                        });
                    }
                }else{
                    Snackbar.make(v,R.string.noEditCommand,Snackbar.LENGTH_LONG).show();
                }
            }
        }, this);

        RecyclerView rvList = findViewById(R.id.rvComandas);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ComandaItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvList);
        adapter.setComandaList(comandasTicket,productos);

        tvMesa=findViewById(R.id.tvMesa);
        tvMesa.setText(tvMesa.getText()+" Nº "+idMesa);

        btFinTicket=findViewById(R.id.btFin);
        btFinTicket.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                secureCancel();
            }
        });

    }

    private void secureCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_secure);
        builder.setMessage(R.string.message_secure);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finTicket();
                if(!comandasTicket.isEmpty()){
                    printRequest();
                }
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void printRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_print);
        builder.setMessage(R.string.message_print);

        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Añado intent a actividad de impresion de factura. 22/11
                Intent printIntent = new Intent(TicketActivity.this, PrintTicketActivity.class);
                printIntent.putExtra("finF", finF);
                startActivity(printIntent);
                finish();
            }
        });

        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void finTicket() {
        boolean fin=false;
        finF=null;
        for (Factura f:facturas) {
            if(f.getId()==facturaActual.getId() && f.getId_employee_finish()==4){
                fin=true;
                finF=f;
            }
        }
        if(fin){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            finF.setFinish_time(LocalDateTime.now().format(formatter));
            finF.setId_employee_finish(idEmpleado);
            finF.setTotal(total);
            viewModel.updateFactura(finF, new WaitResponseServer() {
                @Override
                public void waitingResponse(boolean success, List list) {
                    if(success){
                        if(comandasTicket.isEmpty()){
                            finish();
                        }else {
                            for (Comanda c:comandasTicket
                                 ) {
                                c.setServed(1);
                                viewModel.updateComanda(c,null);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("idMesa",idMesa);
        outState.putLong("idEmpleado",idEmpleado);
        outState.putFloat("total",total);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof ComandaAdapter.MyViewHolder) {
            final Comanda deletedComand = comandasTicket.get(viewHolder.getAdapterPosition());
                // remove the command from recycler view
            //remove command from database and refresh recycler list
            viewModel.deleteCommand(deletedComand, new WaitResponseServer() {
                @Override
                public void waitingResponse(boolean success, List list) {
                    if(success){
                        getCommands();
                        adapter.removeItem(viewHolder.getAdapterPosition());
                    }
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

}
