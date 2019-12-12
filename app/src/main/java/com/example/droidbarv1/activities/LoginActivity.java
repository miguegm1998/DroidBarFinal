package com.example.droidbarv1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.droidbarv1.R;
import com.example.droidbarv1.model.data.Empleado;
import com.example.droidbarv1.model.contract.WaitResponseServer;
import com.example.droidbarv1.view.DroidBarViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class LoginActivity extends AppCompatActivity {


    private Button btSing;
    private TextView tvUsu,tvPass;
    private TextInputLayout textInputLayout,textInputLayout2;
    private DroidBarViewModel viewModel;
    private  List<Empleado>empleados;
    private long idEmpleado=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cargaDatos();
    }

    private void cargaDatos() {
        viewModel =  ViewModelProviders.of(this).get(DroidBarViewModel.class);
        viewModel.getEmpleadosList(new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                empleados=list;
                initComponents();
            }
        });
    }


    private void initComponents() {
        textInputLayout= findViewById(R.id.textInputLayout);
        textInputLayout2=findViewById(R.id.textInputLayout2);
        tvUsu=findViewById(R.id.tvUsu);
        tvPass=findViewById(R.id.tvPass);
        btSing=findViewById(R.id.btSing);
        initEvents();


    }

    private void initEvents() {
        tvUsu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout2.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btSing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(validaUsu(tvUsu.getText().toString())&&validaPass(tvPass.getText().toString())){
                    InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class).putExtra("idEmpleado",idEmpleado));
                }else {
                    if(tvUsu.getText().toString().equalsIgnoreCase("")){
                        textInputLayout.setError(getText(R.string.errorUsuVacio));
                    }else
                    if(!validaUsu(tvUsu.getText().toString())){
                        textInputLayout.setError(getText(R.string.errorUsu));
                    }else if(tvPass.getText().toString().equalsIgnoreCase("")) {
                        textInputLayout2.setError(getText(R.string.contraseñaVacia));
                    }else if(validaUsu(tvUsu.getText().toString())&&!validaPass(tvPass.getText().toString())){
                        textInputLayout2.setError(getText(R.string.errorPass));
                    }
                    else{
                        textInputLayout.setError(getText(R.string.errorUsu));
                    }
                }
            }

        });
    }

    private boolean validaUsu(String usu) {
        for (Empleado e:empleados) {
            if (e.getLogin().equalsIgnoreCase(usu)){
                idEmpleado=e.getId();
                return true;
            }
        }
        return false;
    }


    private boolean validaPass(String pass) {
        //controlar contraseña
        for (Empleado e:empleados
        ) {
            if (e.getPassword().equalsIgnoreCase(pass)){
                return true;
            }
        }
        return false;

    }
}
