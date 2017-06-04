package org.etec.etecapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import org.etec.etecapp.network.JSONHandler;
import org.etec.etecapp.network.RequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CenterSelectionActivity extends AppCompatActivity {

    private ListView centers_list;
    private ArrayAdapter adapter;
    private Button skip_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_selection);

        centers_list = (ListView) findViewById(R.id.centers_selection_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        skip_button = (Button)findViewById(R.id.skip_button);
        request_centers_list();
        centers_list.setAdapter(adapter);
        set_listener();
    }

    /**
     * Listener del menú.
     */
    private void set_listener(){
        centers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_dialog((String)parent.getItemAtPosition(position));
            }
        });
        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_main_menu();
            }
        });
    }

    /**
     * Muestra un mensaje cuando se selecciona un centro de distribución.
     */
    private void show_dialog(final String name){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(name);
        dialog.setMessage("¿Está seguro de su decisión? Considere que una vez hecha la decisión no es posible modificarla");
        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestManager.POST("/log", JSONHandler.build_user_info(LoginActivity.name,LoginActivity.email,name));
                get_main_menu();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Petición para obtener los nombres de los centros de distribución.
     */
    private void request_centers_list(){
        RequestManager.GET("centers");
        RequestManager.wait_for_response(1000);
        process_centers_list(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Procesa los nombres de los centros de distribución.
     * @param list la lista en json.
     */
    private void process_centers_list(String list){
        try {
            JSONObject json = new JSONObject(list);
            JSONArray array = json.getJSONArray("centers");

            for (int i = 0; i < array.length(); i++){
                adapter.add(array.get(i));
            }
        }catch (JSONException j) {
            j.printStackTrace();
        }
    }

    /**
     * Abre la actividad con el menú principal
     */
    private void get_main_menu(){
        Intent main_menu = new Intent(this, MainMenuActivity.class);
        main_menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main_menu);
        finish();
    }
}
