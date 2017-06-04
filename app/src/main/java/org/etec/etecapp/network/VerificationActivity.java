package org.etec.etecapp.network;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.etec.etecapp.CenterSelectionActivity;
import org.etec.etecapp.LoginActivity;
import org.etec.etecapp.MainMenuActivity;
import org.etec.etecapp.R;

public class VerificationActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Espera un momento...");
        dialog.show();
        check_account();
    }

    /**
     * Verifica el estado de la cuenta del usuario en el servidor.
     */
    private void check_account() {
        RequestManager.GET("check/" + LoginActivity.name);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (RequestManager.GET_REQUEST_DATA() != null){
                        if (RequestManager.GET_REQUEST_DATA().equals("{status:notfound}")){
                            dialog.dismiss();
                            get_selection_activity();
                            break;
                        }
                        if (RequestManager.GET_REQUEST_DATA().equals("{status:found}")){
                            dialog.dismiss();
                            get_main_menu();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Abre la actividad donde se escoge el centro de distribución.
     */
    private void get_selection_activity() {
        Intent select_center = new Intent(this, CenterSelectionActivity.class);
        select_center.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(select_center);
        finish();
    }

    /**
     * Lleva al menu principal
     */
    private void get_main_menu(){
        Intent menu = new Intent(this, MainMenuActivity.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(menu);
        finish();
    }
}
