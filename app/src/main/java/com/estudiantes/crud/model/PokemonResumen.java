package com.estudiantes.crud.model;

/**
 * Resumen de un Pokémon obtenido desde la PokeAPI.
 */
public class PokemonResumen {

    private final int apiId;
    private final String nombre;
    private final String imagenUrl;

    public PokemonResumen(int apiId, String nombre, String imagenUrl) {
        this.apiId = apiId;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
    }

    public int getApiId() {
        return apiId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getNombreFormateado() {
        return Pokemon.capitalizar(nombre);
    }
}
