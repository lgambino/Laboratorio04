package dam.isi.frsf.utn.edu.ar.laboratorio04;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.laboratorio04.modelo.Reserva;
import dam.isi.frsf.utn.edu.ar.laboratorio04.utils.BuscarDepartamentosTask;
import dam.isi.frsf.utn.edu.ar.laboratorio04.modelo.Departamento;
import dam.isi.frsf.utn.edu.ar.laboratorio04.utils.BusquedaFinalizadaListener;
import dam.isi.frsf.utn.edu.ar.laboratorio04.utils.FormBusqueda;

public class ListaDepartamentosActivity extends AppCompatActivity implements BusquedaFinalizadaListener<Departamento> {

    private TextView tvEstadoBusqueda;
    private ListView listaAlojamientos;
    private DepartamentoAdapter departamentosAdapter;
    private List<Departamento> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alojamientos);
        lista= new ArrayList<>();
        listaAlojamientos= (ListView ) findViewById(R.id.listaAlojamientos);
        tvEstadoBusqueda = (TextView) findViewById(R.id.estadoBusqueda);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Boolean esBusqueda = intent.getExtras().getBoolean("esBusqueda");
        if(esBusqueda){
            FormBusqueda fb = (FormBusqueda ) intent.getSerializableExtra("frmBusqueda");
            // faltan datos (huespeedes, permite fumar)
            new BuscarDepartamentosTask(ListaDepartamentosActivity.this).execute(fb);
            tvEstadoBusqueda.setText("Buscando....");
            tvEstadoBusqueda.setVisibility(View.VISIBLE);
        }else{
            tvEstadoBusqueda.setVisibility(View.GONE);
            lista=Departamento.getAlojamientosDisponibles();
        }
        departamentosAdapter = new DepartamentoAdapter(ListaDepartamentosActivity.this,lista);
        listaAlojamientos.setAdapter(departamentosAdapter);

        listaAlojamientos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Departamento departamentoAux = (Departamento) listaAlojamientos.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Departamento pendiente de reserva.", Toast.LENGTH_LONG).show();
                TextView sel= (TextView) findViewById(R.id.estadoBusqueda);
                sel.setText(departamentoAux.getDescripcion());

                lista.remove(departamentoAux);
                departamentosAdapter = new DepartamentoAdapter(ListaDepartamentosActivity.this,lista);
                listaAlojamientos.setAdapter(departamentosAdapter);

                List<Reserva> reservas = MainActivity.getReservas();
                Integer nuevoId = 0;
                if(reservas!=null && reservas.size()>0){
                    nuevoId = reservas.get(reservas.size()-1).getId()+1;
                }
                Reserva reservaAux = new Reserva(nuevoId, new Date(), new Date(), departamentoAux);

                MainActivity.addReserva(reservaAux);
            }
        });
    }

    @Override
    public void busquedaFinalizada(List<Departamento> listaDepartamento) {
        tvEstadoBusqueda.setVisibility(View.GONE);
        lista.clear();
        lista=listaDepartamento;
        // TODO por que no funciona el notify solo?
        //departamentosAdapter.notifyDataSetChanged();
        departamentosAdapter = new DepartamentoAdapter(ListaDepartamentosActivity.this,lista);
        listaAlojamientos.setAdapter(departamentosAdapter);
    }

    @Override
    public void busquedaActualizada(String msg) {
        tvEstadoBusqueda.setText(" Buscando..."+msg);
    }

}
