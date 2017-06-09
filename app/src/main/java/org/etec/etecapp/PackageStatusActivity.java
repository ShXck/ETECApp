package org.etec.etecapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.etec.etecapp.network.RequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PackageStatusActivity extends AppCompatActivity {

    private ListView content;
    private ArrayAdapter adapter;
    private TextView status;
    private TextView current_point;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_status);

        content = (ListView)findViewById(R.id.content_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        content.setAdapter(adapter);
        status = (TextView)findViewById(R.id.order_status);
        current_point = (TextView)findViewById(R.id.current_point_text);
        date = (TextView)findViewById(R.id.date_text);
        get_package_details();
    }

    /**
     * Petición para obtener la información del paquete.
     */
    private void get_package_details() {
        RequestManager.GET(LoginActivity.name + "/order");
        RequestManager.wait_for_response(1000);
        process_details(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Procesa los detalles.
     * @param details los detalles en json.
     */
    private void process_details(String details){
        try {
            JSONObject data = new JSONObject(details);
            JSONArray array = data.getJSONArray("products");

            for (int i = 0; i < array.length(); i++){
                adapter.add(array.get(i));
            }

            status.setText("Estado de la orden: " + data.getString("status"));
            date.setText("Fecha estimada de entrega: " + data.getString("date"));
            current_point.setText("Su paquete se encuentra en: " + data.getString("current"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
