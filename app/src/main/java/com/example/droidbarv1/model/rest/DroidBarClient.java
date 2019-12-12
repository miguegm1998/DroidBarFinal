package com.example.droidbarv1.model.rest;

import com.example.droidbarv1.model.data.Empleado;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Producto;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DroidBarClient {
    /////////EMPLEADOS/////////////
    @DELETE("employee/{id}")
    Call<Integer> deleteEmpleado(@Path("id") long id);

    @GET("employee/{id}")
    Call<Empleado> getEmpleado(@Path("id") long id);

    @GET("employee")
    Call<ArrayList<Empleado>> getEmpleado();

    @POST("employee")
    Call<Long> postEmpleado(@Body Empleado empleado);

    @PUT("employee/{id}")
    Call<Integer> putEmpleado(@Path("id") long id, @Body Empleado empleado);

    /////////FACTURAS/////////////

    @DELETE("ticket/{id}")
    Call<Boolean> deleteFactura(@Path("id") long id);

    @GET("ticket/{id}")
    Call<Factura> getFactura(@Path("id") long id);

    @GET("ticket")
    Call<ArrayList<Factura>> getFactura();

    @POST("ticket")
    Call<Long> postFactura(@Body Factura factura);

    @PUT("ticket/{id}")
    Call<Integer> putFactura(@Path("id") long id, @Body Factura factura);

    /////////COMANDAS/////////////

    @DELETE("command/{id}")
    Call<Integer> deleteComanda(@Path("id") long id);

    @GET("command/{id}")
    Call<Comanda> getComanda(@Path("id") long id);

    @GET("command")
    Call<ArrayList<Comanda>> getComanda();

    @POST("command")
    Call<Long> postComanda(@Body Comanda comanda);

    @PUT("command/{id}")
    Call<Integer> putComanda(@Path("id") long id, @Body Comanda comanda);

    /////////PRODUCTO/////////////

    @DELETE("product/{id}")
    Call<Integer> deleteProducto(@Path("id") long id);

    @GET("product/{id}")
    Call<Producto> getProducto(@Path("id") long id);

    @GET("product")
    Call<ArrayList<Producto>> getProducto();

    @POST("product")
    Call<Long> postProducto(@Body Producto producto);

    @PUT("product/{id}")
    Call<Integer> putProducto(@Path("id") long id, @Body Producto producto);




}
