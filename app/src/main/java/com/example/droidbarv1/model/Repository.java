package com.example.droidbarv1.model;

import android.util.Log;

import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Empleado;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.data.Producto;
import com.example.droidbarv1.model.rest.DroidBarClient;
import com.example.droidbarv1.model.contract.WaitResponseServer;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.droidbarv1.activities.MainActivity.TAG;

public class Repository {


    private DroidBarClient apiClient;

    public Repository() {
        retrieveApiClient();
    }
    private void retrieveApiClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://informatica.ieszaidinvergeles.org:8046/DroidBar/public/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiClient=retrofit.create(DroidBarClient.class);

    }

    public void getProductoList(final WaitResponseServer wait) {
        Call<ArrayList<Producto>> call = apiClient.getProducto();
        call.enqueue(new Callback<ArrayList<Producto>>() {
            @Override
            public void onResponse(Call<ArrayList<Producto>> call, Response<ArrayList<Producto>> response) {
                if(response.body()!=null) {
                    wait.waitingResponse(true,response.body());
                }else{
                    wait.waitingResponse(false,null);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Producto>> call, Throwable t) {
            }
        });
    }

    public void geyComandaList(final WaitResponseServer wait) {
        Call<ArrayList<Comanda>> call = apiClient.getComanda();
        call.enqueue(new Callback<ArrayList<Comanda>>() {
            @Override
            public void onResponse(Call<ArrayList<Comanda>> call, Response<ArrayList<Comanda>> response) {
                if(response.body()!=null){
                    wait.waitingResponse(true,response.body());
                }else{
                    wait.waitingResponse(false,null);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Comanda>> call, Throwable t) {
            }
        });
    }

    public void getFacturaList(final WaitResponseServer wait) {
        Call<ArrayList<Factura>> call = apiClient.getFactura();
        call.enqueue(new Callback<ArrayList<Factura>>() {
            @Override
            public void onResponse(Call<ArrayList<Factura>> call, Response<ArrayList<Factura>> response) {
                if(response.body()!=null){
                    wait.waitingResponse(true,response.body());
                }else{
                    wait.waitingResponse(false,null);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Factura>> call, Throwable t) {
            }
        });
    }


    public MutableLiveData<List<Empleado>> getEmpleadoList(final WaitResponseServer wait){
        final MutableLiveData<List<Empleado>> mutableEmpleadoList =new MutableLiveData<>();
        Call<ArrayList<Empleado>> call = apiClient.getEmpleado();
        call.enqueue(new Callback<ArrayList<Empleado>>() {
            @Override
            public void onResponse(Call<ArrayList<Empleado>> call, Response<ArrayList<Empleado>> response) {
                if(response.body()!=null){
                    wait.waitingResponse(true,response.body());
                }else{
                    wait.waitingResponse(false,null);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Empleado>> call, Throwable t) {
            }
        });
        return mutableEmpleadoList;
    }


    public void addFactura(Factura factura, final WaitResponseServer wait) {
        Call<Long> call = apiClient.postFactura(factura);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                    wait.waitingResponse(true,null);
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                wait.waitingResponse(false,null);
            }
        });
    }

    public void addComanda(Comanda comanda, final WaitResponseServer wait){
        Call<Long> call = apiClient.postComanda(comanda);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                wait.waitingResponse(true,null);
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                wait.waitingResponse(false,null);
            }
        });

    }


    public void updateFactura(Factura finF,final WaitResponseServer wait) {
        Call<Integer> call = apiClient.putFactura(finF.getId(),finF);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                System.out.println("Actualiza");
                wait.waitingResponse(true,null);
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    public void updateComanda(Comanda finC,final WaitResponseServer wait) {
        Call<Integer> call = apiClient.putComanda(finC.getId(),finC);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (wait!=null){
                    wait.waitingResponse(true,null);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }


    public void deleteCommand(Comanda deletedComand,final WaitResponseServer wait) {
        //Borrar comanda
        Call<Integer> call = apiClient.deleteComanda(deletedComand.getId());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                wait.waitingResponse(true,null);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
                wait.waitingResponse(false,null);
            }
        });
    }

    public void deleteFactura(Factura deletedFactura) {
        Call<Boolean> call = apiClient.deleteFactura(deletedFactura.getId());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                //fetchFacturaList();
                System.out.println("Borrado");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }
}
