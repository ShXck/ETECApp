package org.etec.etecapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.etec.etecapp.network.JSONHandler;
import org.etec.etecapp.network.RequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultViewActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    private ListView stores_list;
    private Button add_button;
    private TextView product_text;
    private TextView price_text;
    private ArrayAdapter adapter;
    private String[] stores_array;

    private String store_selected;
    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);

        stores_list = (ListView)findViewById(R.id.available_stores_list);
        add_button = (Button)findViewById(R.id.add_to_cart_button);
        product_text = (TextView)findViewById(R.id.product_name);
        price_text = (TextView)findViewById(R.id.cost_text);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        stores_list.setAdapter(adapter);

        RequestManager.wait_for_response(1000);
        set_list();
        set_listeners();
        set_text_views();
    }

    /**
     * Inicializa los textos.
     */
    private void set_text_views() {
        try {
            JSONObject info = new JSONObject(RequestManager.GET_REQUEST_DATA());
            product_text.setText("Product: " + info.getString("product"));
            price_text.setText("Precio: " + info.getString("cost"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa las acciones de los botones.
     */
    private void set_listeners() {
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_stores_dialog();
            }
        });
    }

    /**
     * Muestra una lista de las tiendas para elegir donde comprar.
     */
    private void show_stores_dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Tiendas disponibles");
        builder.setMessage("Selecciona la tienda de tu gusto");

        final ListView list = new ListView(this);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stores_array));
        builder.setView(list);

        final Dialog dialog = builder.create();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                store_selected = (String)parent.getItemAtPosition(position);
                dialog.dismiss();
                show_picker_dialog();
            }
        });
        dialog.show();
    }

    /**
     * Muestra un selector de cantidad.
     */
    private void show_picker_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Cantidad");
        builder.setMessage("Escoge la cantidad");

        final NumberPicker picker = new NumberPicker(this);
        picker.setMaxValue(20);
        picker.setMinValue(1);
        picker.setWrapSelectorWheel(false);
        builder.setView(picker);

        builder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quantity = picker.getValue();
                dialog.dismiss();
                send_product_specs();
                back_to_search();
                Toast.makeText(getApplicationContext(), "Producto añadido a tu carrito", Toast.LENGTH_SHORT).show();
            }
        });

        final Dialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Inicializa los valores de la lista.
     */
    private void set_list() {
        try {
            JSONObject stores = new JSONObject(RequestManager.GET_REQUEST_DATA());
            JSONArray array = stores.getJSONArray("stores");
            stores_array = new String[array.length()];
            for (int k = 0; k < array.length(); k++){
                String current = (String) array.get(k);
                adapter.add(current);
                stores_array[k] = current;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Petición para guardar un producto en el carrito de compras.
     */
    private void send_product_specs(){
        RequestManager.POST(LoginActivity.name + "/add", JSONHandler.build_cart_item_info(SearchProductsActivity.product_selected,quantity,store_selected));
    }

    /**
     * Se devuelve a la ventana de búsqueda.
     */
    private void back_to_search(){
        Intent search = new Intent(this, SearchProductsActivity.class);
        search.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(search);
        finish();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}
