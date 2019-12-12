package com.example.droidbarv1.view;

import android.app.Application;

import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.Repository;
import com.example.droidbarv1.model.contract.WaitResponseServer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class DroidBarViewModel extends AndroidViewModel {
    private Repository repository;

    public DroidBarViewModel(@NonNull Application application){
        super(application);
        repository= new Repository();

    }

    //Gets Listas
    public void getEmpleadosList(final WaitResponseServer wait){
         repository.getEmpleadoList(wait);
    }
    public void getFacturaList(final WaitResponseServer wait){
         repository.getFacturaList(wait);
    }
    public void getComandaList(final WaitResponseServer wait){
         repository.geyComandaList(wait);
    }
    public void getProductoList(final WaitResponseServer wait){
         repository.getProductoList(wait);
    }

    //Adds
    public void addFactura(Factura factura,final WaitResponseServer wait){
        repository.addFactura(factura,wait);
    }
    public void addComanda(Comanda comanda, final WaitResponseServer wait){
        repository.addComanda(comanda, wait);
    }


    //Updates
    public void updateFactura(Factura finF,final WaitResponseServer wait) {
        repository.updateFactura(finF,wait);
    }
    public void updateComanda(Comanda finC,final WaitResponseServer wait){
        repository.updateComanda(finC,wait);
    }

    //Deletes
    public void deleteCommand(Comanda deletedComand,final WaitResponseServer wait) {
        repository.deleteCommand(deletedComand,wait);
    }

    public void deleteFactura(Factura deletedFactura) {
        repository.deleteFactura(deletedFactura);
    }
}
