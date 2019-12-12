package com.example.droidbarv1.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droidbarv1.R;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.contract.WaitResponseServer;
import com.example.droidbarv1.view.DroidBarViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    public static final String TAG ="xxx";
    private List<Factura> facturas;
    private ArrayList<Integer> mesasLLenas;

    private DroidBarViewModel viewModel;

    private long idEmpleado=0;
    private int idMesa;

    private Intent i;

    private ImageView iv;
    private View fragmento;
    private Boolean añadida=false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        añadida=false;
        viewModel.getFacturaList(new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                if(success){
                    facturas= list;
                    //pintamos mesas llenas o vacias segun si tienen facturas abiertas->Empleado de cierre=4
                    putTables();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Redefinido para evitar volver al login
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel =  ViewModelProviders.of(this).get(DroidBarViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(R.string.titulo);
        setSupportActionBar(toolbar);
        initNavigationDrawer(toolbar);

        //cargar datos
        cargarDatos();

        //Recogemos el id del empleado logeado
        this.idEmpleado=getIntent().getLongExtra("idEmpleado",0);

        //Fragmento loading screen
        fragmento = findViewById(R.id.fragmentLoadingMain);
        fragmento.setVisibility(View.INVISIBLE);
    }

    private void cargarDatos() {
        viewModel.getFacturaList(new WaitResponseServer() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void waitingResponse(boolean success, List list) {
                if(success){
                    facturas= list;
                    //pintamos mesas llenas o vacias segun si tienen facturas abiertas->Empleado de cierre=4
                    putTables();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void putTables() {
        //Array para saber los numeros de las mesas llenas
        if(facturas!=null){
            mesasLLenas=new ArrayList<>();
            for (Factura f:facturas) {
                if(f.getId_employee_finish()==4){
                    iv=findViewById(R.id.clMain).findViewWithTag(String.valueOf(f.getTable()));
                    mesasLLenas.add(f.getTable());
                    if(f.getTable()<10){
                        iv.setImageDrawable(getDrawable(R.drawable.table));
                    }else{
                        iv.setImageDrawable(getDrawable(R.drawable.stool));
                    }
                }else{
                    iv=findViewById(R.id.clMain).findViewWithTag(String.valueOf(f.getTable()));
                    if(f.getTable()<10){
                        iv.setImageDrawable(getDrawable(R.drawable.table_empty));
                    }else{
                        iv.setImageDrawable(getDrawable(R.drawable.stool_empty));
                    }
                }
            }
        } else {
            cargarDatos();
        }
    }

    private void initNavigationDrawer(Toolbar toolbar) {
        //Crear toggle para abrir el navigation drawer
        DrawerLayout drawerLayout =findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView= findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                System.out.println("Seleccionado: "+id);
                switch (id){
                    case (R.id.menu_history):
                        //intent history
                        startActivity(new Intent(MainActivity.this,HistorialActivity.class));
                        break;
                    case (R.id.menu_logout):
                        //intent logOut
                        finish();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        break;
                }
                return false;
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View v) {
        //Metodo onclick para los ImageView de las mesas, asignado en layout para evitar codigo
        idMesa =Integer.parseInt(v.getTag().toString());
        //Creamos intent para lanzar la sig actividad
        i = new Intent(this, TicketActivity.class).putExtra("idEmpleado",idEmpleado);
        //Metodo de creacion de factura, si la mesa esta vacia es decir no esta en el array de mesasLLenas
        if(!mesasLLenas.contains(idMesa)){
            //cargar load view task
            final Factura factObtenida = creaFactura(idMesa);
            new LoadViewTask().execute();
            addFacturaServer(factObtenida);
            //Lanzamos activity de ticket, con un boolean indicando que no se crea ticket

        }else{
            //Metodo para poner la factura correspondiente en la activity de TicketActivity
            Factura factObtenida= obtenFactura();
            //Lanzamos activity de ticket, con un boolean indicando que no se crea ticket
            startActivity(i.putExtra("factura",factObtenida));
        }
    }
    private void addFacturaServer(final Factura factObtenida){
        viewModel.addFactura(factObtenida, new WaitResponseServer() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void waitingResponse(boolean success, List list) {
                if(success){
                    añadida=true;
                    fragmento.setVisibility(View.INVISIBLE);
                    startActivity(i.putExtra("factura",factObtenida));
                }else{
                    cargarDatos();
                    Factura factObtenida= obtenFactura();
                    startActivity(i.putExtra("factura",factObtenida));
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Factura creaFactura(int idMesa) {
        //Creamos nueva Factura
            Factura newF = new Factura();
            newF.setTable(idMesa);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            newF.setStart_time(LocalDateTime.now().format(formatter));
            newF.setId_employee_start(idEmpleado);
           return newF;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Factura obtenFactura() {
        Factura factObtenida=null;
        for (Factura f:facturas) {
            if(f.getTable()==idMesa&&f.getId_employee_finish()==4){
                factObtenida = f;
            }
        }
        return factObtenida;
    }



    private class LoadViewTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragmento.setVisibility(View.VISIBLE);

        }
        @Override
        protected Void doInBackground(final Void... voids) {

            while(!añadida){

            }
            return null;
        }
        protected void onPostExecute(Void result){
            fragmento.setVisibility(View.INVISIBLE);
        }
    }

}
