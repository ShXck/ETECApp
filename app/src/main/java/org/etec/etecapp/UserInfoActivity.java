package org.etec.etecapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.etec.etecapp.network.RequestManager;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfoActivity extends AppCompatActivity {

    private TextView center_name;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        center_name = (TextView)findViewById(R.id.center_name);
        email = (TextView)findViewById(R.id.email_user);
        request_user_info();
    }

    /**
     * Petición para obtener la información del usuario.
     */
    private void request_user_info() {
        RequestManager.GET(LoginActivity.name + "/info");
        RequestManager.wait_for_response(1000);
        set_textviews(RequestManager.GET_REQUEST_DATA());
    }

    /**
     * Pone la información el los textviews.
     * @param info la información en json.
     */
    private void set_textviews(String info){
        try {
            JSONObject j = new JSONObject(info);
            center_name.setText("Centro de distribución: " + j.getString("center"));
            email.setText("Correo: " + j.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
