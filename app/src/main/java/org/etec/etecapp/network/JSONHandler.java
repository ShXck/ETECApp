package org.etec.etecapp.network;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONHandler {

    /**
     * Construye un json con la información inicial del usuario.
     * @param name el nombre.
     * @param email el correo.
     * @param center_selected el centro de distribución seleccionado.
     * @return un json con la información.
     */
    public static String build_user_info(String name, String email, String center_selected){
        JSONObject info = new JSONObject();
        try{
            info.put("name",name);
            info.put("email",email);
            info.put("center",center_selected);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return info.toString();
    }

    /**
     * Construye un json con las especificaciones de la búsqueda de productos.
     * @return el json con las especificaciones.
     */
    public static String build_search_specs(String sort_algo, String search_algo, String attribute, String product_name){

        JSONObject specs = new JSONObject();

        try {
            specs.put("sort", sort_algo);
            specs.put("search",search_algo);
            specs.put("attribute",attribute);
            specs.put("product",product_name);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return specs.toString();
    }
}
