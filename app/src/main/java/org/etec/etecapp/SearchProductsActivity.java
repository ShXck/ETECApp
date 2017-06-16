package org.etec.etecapp;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.etec.etecapp.network.JSONHandler;
import org.etec.etecapp.network.RequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchProductsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView search_view;
    private ListView products_list;
    private ArrayAdapter adapter;
    private ImageButton speak_button;

    private String sort_algo;
    private String search_algo;
    public static String product_selected;
    private String order;

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_search_products);

        search_view = (SearchView)findViewById(R.id.search_panel);
        products_list = (ListView)findViewById(R.id.products_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        speak_button = (ImageButton)findViewById(R.id.speak_button);
        products_list.setAdapter(adapter);
        products_list.setTextFilterEnabled(true);

        request_list();
        set_search_view();
        set_listeners();
    }

    private void set_listeners() {
        products_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                product_selected = (String)parent.getItemAtPosition(position);
                show_sort_method_options();
            }
        });

        speak_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
    }

    /**
     * Establece la configuración de la búsqueda.
     */
    private void set_search_view() {
        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(this);
        search_view.setQueryHint("¿Qué es lo que buscas?");
    }

    /**
     * Petición para obtener la lista de productos.
     */
    private void request_list(){
        RequestManager.GET("products");
        RequestManager.wait_for_response(1000);
        process_products_list(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Procesa la lista de productos
     * @param list la lista en json.
     */
    private void process_products_list(String list){
        try {
            JSONObject j = new JSONObject(list);
            JSONArray array = j.getJSONArray("products");

            for (int i = 0; i < array.length(); i++){
                adapter.add(array.get(i));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Muestra las opciones para seleccionar un método de ordenamiento.
     */
    public void show_sort_method_options(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Métodos de ordenamiento");
        builder.setMessage("Elige un algoritmo para ordenar");

        final String[] options = {"Selection Sort", "Bubble Sort", "Insertion Sort", "Shell Sort", "Merge Sort", "Quick Sort", "Radix Sort"};
        final ListView list = new ListView(this);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));
        builder.setView(list);

        final Dialog dialog = builder.create();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sort_algo = (String)parent.getItemAtPosition(position);
                show_search_method_options();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Muestra las opciones de algoritmos de búsqueda.
     */
    public void show_search_method_options(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Métodos de Búsqueda");
        builder.setMessage("Elige un algoritmo para buscar");

        final String[] options = {"Binary Search" , "Interpolation Search"};
        final ListView list = new ListView(this);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));
        builder.setView(list);

        final Dialog dialog = builder.create();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search_algo = (String)parent.getItemAtPosition(position);
                show_order_options();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Muestra las opciones de atributos para orden de elementos.
     */
    public void show_order_options(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Orden");
        builder.setMessage("Elige un orden para los elementos");

        final String[] options = {"Ascendente", "Descendente"};
        final ListView list = new ListView(this);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));
        builder.setView(list);

        final Dialog dialog = builder.create();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                order = (String)parent.getItemAtPosition(position);
                dialog.dismiss();
                send_specs();
                show_search_results();
            }
        });
        dialog.show();
    }

    /**
     * Abre la ventana con los resultados de la búsqueda.
     */
    private void show_search_results(){
        Intent show_results = new Intent(this, ResultViewActivity.class);
        startActivity(show_results);
    }

    /**
     * Petición para buscar el producto y obtener la información.
     */
    public void send_specs(){
        RequestManager.POST("search", JSONHandler.build_search_specs(sort_algo,search_algo, order, product_selected));
    }

    /**
     * Inicia el reconocimiento por voz.
     */
    public void record(){
        Intent intentActionRecognizeSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentActionRecognizeSpeech.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
        try {
            startActivityForResult(intentActionRecognizeSpeech, RECOGNIZE_SPEECH_ACTIVITY);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Tú dispositivo no soporta el reconocimiento por voz", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:

                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String strSpeech2Text = speech.get(0);
                    check_recorded_product(strSpeech2Text.toLowerCase());
                    Log.i("Recorded", strSpeech2Text);
                }
                break;
        }
    }

    /**
     * Verifica que el producto que se grabó, se encuentra en la lista.
     */
    private void check_recorded_product(String recorded_txt){

        boolean found = false;

        for (int i = 0; i < adapter.getCount(); i++){
            String current = (String) adapter.getItem(i);
            if (current.toLowerCase().equals(recorded_txt)){
                product_selected = (String) adapter.getItem(i);
                found = true;
                show_sort_method_options();
                break;
            }
        }

        if (!found) Toast.makeText(getApplicationContext(), recorded_txt + " no fue encontrado, intenta de nuevo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)){
            products_list.clearTextFilter();
        }else {
            products_list.setFilterText(newText.toString());
        }
        return true;
    }
}
