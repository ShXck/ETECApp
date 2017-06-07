package org.etec.etecapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.etec.etecapp.network.JSONHandler;
import org.etec.etecapp.network.RequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShoppingCartActivity extends AppCompatActivity {

    private ListView products_list;
    private Button buy_button;
    private TextView total_label;
    private ArrayAdapter adapter;

    private int new_quantity;
    private String product_changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        products_list = (ListView)findViewById(R.id.cart_list);
        buy_button = (Button)findViewById(R.id.buy_button);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        total_label = (TextView)findViewById(R.id.total_text);
        products_list.setAdapter(adapter);
        get_user_cart();
        set_listeners();
    }

    /**
     * Maneja los listener de la lista y el boton.
     */
    private void set_listeners() {
        buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_payment_method_dialog();
            }
        });

        products_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                product_changed = (String) parent.getItemAtPosition(position);
                show_picker_dialog();
            }
        });
    }

    /**
     * Obtiene el carrito del usuario en el servidor.
     */
    private void get_user_cart() {
        RequestManager.GET(LoginActivity.name + "/cart");
        RequestManager.wait_for_response(1000);
        process_cart(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Procesa la información json del carro.
     * @param cart_info la información.
     */
    private void process_cart(String cart_info){
        try {
            JSONObject info = new JSONObject(cart_info);
            JSONArray products = info.getJSONArray("products");
            JSONArray quantities = info.getJSONArray("quantities");

            for (int i = 0; i < products.length(); i++){
                adapter.add(products.get(i) + "\n Cantidad: " + quantities.get(i));
            }
            total_label.setText("Total: " + info.getString("total"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                new_quantity = picker.getValue();
                modify_item();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final Dialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Muestra una lista con los métodos de pago.
     */
    private void show_payment_method_dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Métodos de pago");
        builder.setMessage("Selecciona un método de pago.");

        String[] options = {"Tarjeta de crédito" , "Tarjeta de débito", "Paypal", "Bitcoin"};
        final ListView list = new ListView(this);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));
        builder.setView(list);

        final Dialog dialog = builder.create();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_pass_dialog();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Muestra una lista de las tiendas para elegir donde comprar.
     */
    private void show_pass_dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Escribe tu código de cuenta");
        builder.setMessage("Por favor, inserta tu código de cuenta para realizar la transacción");

        final EditText pass_text = new EditText(this);
        builder.setView(pass_text);

        builder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request_buy();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final Dialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Modifica el carrito de compras en el servidor..
     */
    private void modify_item(){
        RequestManager.PUT(LoginActivity.name + "/cart", JSONHandler.build_modified_item_info(product_changed,new_quantity));
        update();
    }

    /**
     * Petición para iniciar el desplazamiento del paquete.
     */
    private void request_buy(){
        RequestManager.POST(LoginActivity.name + "buy","");
    }

    /**
     * Actualiza la actividad.
     */
    private void update(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


}
