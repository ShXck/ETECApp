package org.etec.etecapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.etec.etecapp.network.RequestManager;
import org.etec.etecapp.network.VerificationActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton button;
    private CallbackManager callbackManager;

    public static String name;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        button = (LoginButton) findViewById(R.id.login_button);
        button.setReadPermissions(Arrays.asList("email"));
        check_login_status();
    }

    /**
     * Listener del botón de login.
     */
    private void check_login_status() {
        button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                get_user_info(loginResult);
                get_verification_activity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    /**
     * Lleva a la ventana de verificación.
     */
    private void get_verification_activity(){
        Intent verification = new Intent(this, VerificationActivity.class);
        verification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(verification);
        finish();
    }

    /**
     * Maneja el resultado del login.
     * @param requestCode el código.
     * @param resultCode el código resultante.
     * @param data la actividad.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Obtiene la información dle usuario a través de la API de facebook.
     * @param result el resultado del login.
     */
    private void get_user_info(LoginResult result) {
        AccessToken token = result.getAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.v("response:", response.toString());

                try {
                    //RequestManager.POST("log", object.toString());
                    name = object.getString("name");
                    email = object.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameter = new Bundle();
        parameter.putString("fields", "name, email");
        request.setParameters(parameter);
        request.executeAsync();
    }
}
