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
    public static String build_search_specs(String sort_algo, String search_algo, String order, String product_name){

        JSONObject specs = new JSONObject();

        try {
            specs.put("sort", sort_algo);
            specs.put("search",search_algo);
            specs.put("product",product_name);
            specs.put("order", order);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return specs.toString();
    }

    /**
     * Construye un json con la información del producto que se añadió al carrito de compras.
     * @return el json con la información.
     */
    public static String build_cart_item_info(String name, int quatity, String store){
        JSONObject info = new JSONObject();
        try{
            info.put("product", name);
            info.put("quantity",quatity);
            info.put("store", store);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return info.toString();
    }

    /**
     * Construye la información del producto con modificaciones.
     * @param changed el producto cambiado.
     * @param quantity la nueva cantidad.
     * @return la información en json.
     */
    public static String build_modified_item_info(String changed, int quantity){
        JSONObject info = new JSONObject();
        try {
            info.put("product",changed);
            info.put("quantity",quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info.toString();
    }
}
