package org.etec.etecapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.etec.etecapp.R;

public class MainMenuActivity extends AppCompatActivity {

    private ListView menu;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        String[] options = {"Buscar Productos", "Mi carrito","Ver estado del paquete","Mi información"};
        menu = (ListView)findViewById(R.id.main_menu);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,options);
        menu.setAdapter(adapter);
        set_listener();
    }

    /**
     * Listener del menú.
     */
    private void set_listener(){
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent search_products = new Intent(MainMenuActivity.this, SearchProductsActivity.class);
                        startActivity(search_products);
                        break;
                    case 1:
                        Intent shopping_cart = new Intent(MainMenuActivity.this, ShoppingCartActivity.class);
                        startActivity(shopping_cart);
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent user_info = new Intent(MainMenuActivity.this, UserInfoActivity.class);
                        startActivity(user_info);
                        break;
                }
            }
        });
    }
}
