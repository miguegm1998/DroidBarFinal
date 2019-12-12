package com.example.droidbarv1.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;

import com.example.droidbarv1.R;
import com.example.droidbarv1.model.data.Comanda;
import com.example.droidbarv1.model.data.Empleado;
import com.example.droidbarv1.model.data.Factura;
import com.example.droidbarv1.model.data.Producto;
import com.example.droidbarv1.model.contract.WaitResponseServer;
import com.example.droidbarv1.view.DroidBarViewModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintTicketActivity extends AppCompatActivity {

    private boolean ultimaPagina = false;
    private DroidBarViewModel viewModel;
    private int contador=0;
    private List<Empleado> empleados;
    private List<Comanda>comandasTotal;
    private List<Comanda> comandas, comandasImprimir, comandasAgrupadas;
    private List<Producto> productos;
    private long idTicket;
    private long idEmpleado;
    private String nomEmpleado;
    private int idMesaF, numPags;
    private String finish_time, nombreProductoAux;
    private float total;
    private View fragmento;
    private int cont=1;
    private Factura facturaActual;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_ticket);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        fragmento = findViewById(R.id.fragmentLoadingPrint);
        fragmento.setVisibility(View.INVISIBLE);

        viewModel =  ViewModelProviders.of(this).get(DroidBarViewModel.class);

        getDataAndInit();
    }

    private void getDataAndInit() {
        viewModel.getComandaList(new WaitResponseServer() {
            @Override
            public void waitingResponse(boolean success, List list) {
                if (success){
                    comandasTotal=list;
                    System.out.println("RECUPERA COMANDAS: "+comandasTotal.toString());
                    getParcelable();
                    agruparComandas();
                    viewModel.getEmpleadosList(new WaitResponseServer() {
                        @Override
                        public void waitingResponse(boolean success, List list) {
                            if(success){
                                empleados=list;
                                seleccionaEmpleado(idEmpleado);
                                viewModel.getProductoList(new WaitResponseServer() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void waitingResponse(boolean success, List list) {
                                        productos=list;
                                        numPags = calcularNumPags();
                                        doPrint();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void getParcelable() {
        Intent i = getIntent();
        facturaActual = i.getParcelableExtra("finF");
        idTicket = facturaActual.getId();
        recuperaComandas();
        idEmpleado = facturaActual.getId_employee_finish();
        idMesaF = facturaActual.getTable();
        finish_time = facturaActual.getFinish_time();
        total = facturaActual.getTotal();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cont>1){
            finish();
        }else{
            cont++;
        }
    }


    private void recuperaComandas() {
        comandas= new ArrayList<>();
        for (Comanda c:comandasTotal
        ) {
            if(c.getId_ticket()==facturaActual.getId()){
                comandas.add(c);
            }
        }
    }

    private void seleccionaEmpleado(long idEmpleado) {
        for(Empleado empAux : empleados){
            if(idEmpleado == empAux.getId()){
                nomEmpleado = empAux.getLogin();
            }
        }
    }

    private int calcularNumPags(){
        int numComandas = comandasAgrupadas.size();
        System.out.println(numComandas);
        int numPags = numComandas / 8;
        if(numComandas % 8 == 0){
            return numPags;
        }else{
            numPags += 1;
            return numPags;
        }

    }

    private void recargarComandasImprimir(){
        Comanda comandaAux;
        comandasImprimir = new ArrayList<>();
        for(int i = contador; i< comandasAgrupadas.size() && i < contador+8 ; i++ ){
            comandaAux = comandasAgrupadas.get(i);
            comandasImprimir.add(comandaAux);
        }
        if(comandasImprimir.size() < 8){
            ultimaPagina = true;
        }
        contador +=8;
    }

    private void agruparComandas(){
        comandasAgrupadas = new ArrayList<>();
        boolean añadida;
        Comanda comandaAux, comandaAux2;
        for(int i = 0; i < comandas.size(); i++){
            comandaAux2 = comandas.get(i);
            añadida = false;
            if(comandasAgrupadas.size() == 0){
                comandasAgrupadas.add(comandaAux2);
            }else{
                for(int j = 0; j < comandasAgrupadas.size(); j++){
                    if(comandasAgrupadas.get(j).getId_product() == comandaAux2.getId_product()){
                        comandaAux = comandasAgrupadas.get(j);
                        comandaAux.setUnits(comandaAux.getUnits()+comandaAux2.getUnits());
                        comandaAux.setPrice(comandaAux.getPrice()+comandaAux2.getPrice());
                        comandasAgrupadas.set(j, comandaAux);
                        añadida = true;
                    }
                }
                if(!añadida){
                    comandasAgrupadas.add(comandaAux2);
                }

            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void doPrint(){
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) + " Document";

        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        private int pageHeight;
        private int pageWidth;
        private PrintedPdfDocument myPdfDocument;
        private int totalPages = numPags;

        public MyPrintDocumentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            myPdfDocument = new PrintedPdfDocument(context, newAttributes);
            pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            // Compute the expected number of printed pages
            //totalPages = computePageCount(newAttributes);

            if (numPags > 0) {
                PrintDocumentInfo.Builder builder = new
                        PrintDocumentInfo
                                .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(numPags);
                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

            for (int i = 0; i < numPags; i++) {

                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();
                PdfDocument.Page page = myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                recargarComandasImprimir();
                drawPage(page);
                myPdfDocument.finishPage(page);

            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }
            callback.onWriteFinished(pages);


        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void drawPage(PdfDocument.Page page) {
            Canvas canvas = page.getCanvas();

            // units are in points (1/72 of an inch)
            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);


            Typeface tf = getResources().getFont(R.font.whitesmithfont);

            Paint paintlogo = new Paint();
            paintlogo.setTypeface(tf);
            paintlogo.setColor(Color.BLACK);
            paintlogo.setTextSize(80);
            canvas.drawText("Droidbar", leftMargin+150, titleBaseLine, paintlogo);

            canvas.drawText("Factura "+ idTicket, leftMargin, titleBaseLine+60, paint);

            paint.setColor(Color.GRAY);

            canvas.drawRect(leftMargin, 278, 550, 280, paint);




            paint.setTextSize(24);

            paint.setColor(Color.BLACK);

            canvas.drawText("Empleado: "+ nomEmpleado, leftMargin, titleBaseLine + 120, paint);

            canvas.drawText("Hora: "+ finish_time, leftMargin, titleBaseLine + 160, paint);

            canvas.drawText("Producto", leftMargin, titleBaseLine + 200, paint);

            canvas.drawText("Uds.", leftMargin+360, titleBaseLine + 200, paint);

            canvas.drawText("Precio", leftMargin+420, titleBaseLine + 200, paint);

            // Bucle productos

            int varY = 240;

            for(Comanda comandaAux : comandasImprimir){
                for(Producto productAux : productos){
                    if(productAux.getId() == comandaAux.getId_product()){
                        nombreProductoAux = productAux.getName();
                    }
                }
                paint.setColor(Color.GRAY);

                canvas.drawText(nombreProductoAux, leftMargin, titleBaseLine + varY, paint);

                canvas.drawText(""+comandaAux.getUnits(), leftMargin+370, titleBaseLine + varY, paint);

                canvas.drawText(""+comandaAux.getPrice()+"€", leftMargin+436, titleBaseLine + varY, paint);

                varY += 50;
            }


            if(ultimaPagina){
                varY += 50;

                canvas.drawRect(leftMargin, varY-2, 550, varY, paint);
                paint.setColor(Color.BLACK);

                canvas.drawText("TOTAL: ", leftMargin, titleBaseLine + varY, paint);

                canvas.drawText(""+total+"€", leftMargin+436, titleBaseLine + varY, paint);
            }
            /*---------------------------------------*/



        }
    }

}
