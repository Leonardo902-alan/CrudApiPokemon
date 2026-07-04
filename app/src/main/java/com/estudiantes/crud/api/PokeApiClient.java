package com.estudiantes.crud.api;

import com.estudiantes.crud.model.PokemonDetalle;
import com.estudiantes.crud.model.PokemonResumen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente HTTP para consumir la PokeAPI (https://pokeapi.co).
 * Utiliza HttpURLConnection sin librerías externas de red.
 */
public class PokeApiClient {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private static final int TIMEOUT = 15000;

    private PokeApiClient() {
    }

    /**
     * Obtiene una lista paginada de Pokémon desde la API.
     */
    public static List<PokemonResumen> obtenerListaPokemon(int offset, int limit) throws Exception {
        String url = BASE_URL + "pokemon?offset=" + offset + "&limit=" + limit;
        JSONObject json = ejecutarGet(url);
        JSONArray results = json.getJSONArray("results");
        List<PokemonResumen> lista = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            String nombre = item.getString("name");
            int apiId = extraerIdDesdeUrl(item.getString("url"));
            String imagenUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                    + apiId + ".png";
            lista.add(new PokemonResumen(apiId, nombre, imagenUrl));
        }

        return lista;
    }

    /**
     * Busca un Pokémon por nombre en la PokeAPI.
     */
    public static PokemonDetalle obtenerDetallePokemon(String nombre) throws Exception {
        String url = BASE_URL + "pokemon/" + nombre.toLowerCase().trim();
        JSONObject json = ejecutarGet(url);
        return parsearDetalle(json);
    }

    /**
     * Obtiene el detalle de un Pokémon por su ID numérico de la API.
     */
    public static PokemonDetalle obtenerDetallePorId(int apiId) throws Exception {
        String url = BASE_URL + "pokemon/" + apiId;
        JSONObject json = ejecutarGet(url);
        return parsearDetalle(json);
    }

    private static PokemonDetalle parsearDetalle(JSONObject json) throws Exception {
        PokemonDetalle detalle = new PokemonDetalle();
        detalle.setApiId(json.getInt("id"));
        detalle.setNombre(json.getString("name"));
        detalle.setAltura(json.getInt("height"));
        detalle.setPeso(json.getInt("weight"));

        JSONObject sprites = json.getJSONObject("sprites");
        String imagenUrl = sprites.optString("front_default", "");
        if (imagenUrl == null || imagenUrl.isEmpty()) {
            imagenUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                    + json.getInt("id") + ".png";
        }
        detalle.setImagenUrl(imagenUrl);

        JSONArray tipos = json.getJSONArray("types");
        List<String> listaTipos = new ArrayList<>();
        for (int i = 0; i < tipos.length(); i++) {
            JSONObject tipoObj = tipos.getJSONObject(i).getJSONObject("type");
            listaTipos.add(tipoObj.getString("name"));
        }
        detalle.setTipos(listaTipos);

        JSONArray abilities = json.getJSONArray("abilities");
        List<String> listaHabilidades = new ArrayList<>();
        for (int i = 0; i < abilities.length(); i++) {
            JSONObject abilityObj = abilities.getJSONObject(i).getJSONObject("ability");
            listaHabilidades.add(abilityObj.getString("name"));
        }
        detalle.setHabilidades(listaHabilidades);

        return detalle;
    }

    private static int extraerIdDesdeUrl(String url) {
        String[] partes = url.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }

    private static JSONObject ejecutarGet(String urlString) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Error HTTP: " + responseCode);
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return new JSONObject(response.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
