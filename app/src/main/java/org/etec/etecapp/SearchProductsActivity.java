package org.etec.etecapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;


public class SearchProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_search_products);

    }



}
