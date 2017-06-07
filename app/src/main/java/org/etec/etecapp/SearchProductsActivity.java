package org.etec.etecapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.etec.etecapp.network.JSONHandler;
import org.etec.etecapp.network.RequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchProductsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView search_view;
    private ListView products_list;
    private ArrayAdapter adapter;

    private String sort_algo;
    private String search_algo;
    public static String product_selected;
    private String order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_search_products);

        search_view = (SearchView)findViewById(R.id.search_panel);
        products_list = (ListView)findViewById(R.id.products_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        products_list.setAdapter(adapter);
        products_list.setTextFilterEnabled(true);

        request_list();
        set_search_view();
        set_list_listener();
    }

    private void set_list_listener() {
        products_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                product_selected = (String)parent.getItemAtPosition(position);
                show_sort_method_options();
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
