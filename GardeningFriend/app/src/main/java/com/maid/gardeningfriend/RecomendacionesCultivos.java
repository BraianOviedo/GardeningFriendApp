package com.maid.gardeningfriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Map;

/**
 * segunda pantalla de "recomendaciones"
 * se muestran los cultivos filtrados
 */
public class RecomendacionesCultivos extends MainActivity implements RecomendacionesInterface{

    //variables que contienen los parametros selec
    String tempSelec;
    String estSelec;
    String regSelec;
    // array que contiene los cultivos que coinciden con los parametros
    ArrayList<CultivosGenerador> cultivosFiltrados = new ArrayList<>();
    int[] imagenesCultivos = {R.mipmap.ic_aceituna, R.mipmap.ic_calabaza, R.mipmap.ic_cebolla, R.mipmap.ic_lechuga, R.mipmap.logo};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones_cultivos);

        //se recibe el objeto parceable con los datos
        CultivosReco opcSeleccionadas = getIntent().getParcelableExtra("datosReco");

        // se reciben los datos selec en la pantalla anterior
        tempSelec = opcSeleccionadas.getTemperaturaSelec();
        estSelec = opcSeleccionadas.getEstacionSelec();
        regSelec = opcSeleccionadas.getRegSelec();

        //se inicializa funcion para agregar las tarjetas
        addModelsCultivos();

    }

    /**
     * agrega los cultivos que coinciden con los param selecionados por el user
     * @return cultivos que se añaden a un array
     */
    private void addModelsCultivos(){
        // 1 - se crea una instancia de la BD para acceder a la coleccion
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 2 - se realiza una get request para consumir los datos de los cultivos
        db.collection("cultivos")
                .get()
                //listener que verifica si la peticion fue exitosa
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.i("tag","peticion exitosa");
                            // se recorre la coleccion y se extraen los datos necesarios
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // se guarda la info consumida en un objeto map
                                Map<String, Object> data = document.getData();

                                // se extraen las propiedades del elem iterado
                                String nombre = (String) data.get("nombre");
                                String temperatura = (String) data.get("temperatura");
                                String estacion = (String) data.get("estacion");
                                String region = (String) data.get("region");
                                String informacion = (String) data.get("informacion");
                                String crecimiento = (String) data.get("crecimiento");
                                String tipo = (String) data.get("tipo");

                                // se compara con la seleccion del usuario
                                if(tempSelec.equals(temperatura) &&
                                estSelec.equals(estacion)&&
                                regSelec.equals(region)){
                                    // se crea objeto para agregar cultivo al array
                                    CultivosGenerador nuevoCultivo = new CultivosGenerador(
                                            nombre,
                                            nombre,
                                            tipo,
                                            crecimiento,
                                            informacion,
                                            temperatura,
                                            estacion,
                                            region,
                                            imagenesCultivos[4]);

                                    agregarCultivo(nuevoCultivo);
                                }
                            }
                            // una vez que se iteran todos los documentos se activa el recyclerview adapter
                            activarAdapter();

                            Log.i("tag", "tamaño array: " + cultivosFiltrados.size());
                        } else {
                            // fracaso la peticion & se muestra mensaje
                            Toast.makeText(RecomendacionesCultivos.this,"hubo un error al conectarse con la BD", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * debido a que la get request es una
     * funcion asincronica el objeto de cada cultivo
     * filtrado se añade aparte
     * @param cultivoSelec
     */
    public void agregarCultivo(CultivosGenerador cultivoSelec){
        cultivosFiltrados.add(cultivoSelec);
        Log.i("tag", "cultivo agreagado: " + cultivoSelec.nombre);
    }

    /**
     * activa el adapter de recycler view
     * para pasar todos los cultivos
     * guardados en el array "cultivosFiltrados"
     */
    public void activarAdapter(){
        //se identifica el recyclerview de la activity
        RecyclerView recycler = findViewById(R.id.recycler_cultivos);

        // se activa el "adapter" para que pase las tarjetas al recycler
        RecomendacionesRecyclerView adapter = new RecomendacionesRecyclerView(this,cultivosFiltrados,this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }


    /**
     * pasa los valores de la tarjeta seleccionada
     * a la siguiente vista donde se muestran los detalles
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        // 1 - se crea intent para dirigir al user a la pantalla con info extra:
        Intent intent = new Intent(RecomendacionesCultivos.this, RecomendacionesDetalles.class);

        // 2 - se pasan las propiedades necesarias:
        intent.putExtra("NOMBRE_CULTIVO", cultivosFiltrados.get(position).getNombre());
        intent.putExtra("INFO_CULTIVO", cultivosFiltrados.get(position).getCaracteristicas());

        // 3 - se inicialza la nueva actividad (intent)
        startActivity(intent);
    }




}