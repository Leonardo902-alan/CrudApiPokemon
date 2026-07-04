package com.estudiantes.crud.model;

/**
 * Entidad local almacenada en SQLite (Pokémon capturados en la Pokédex personal).
 */
public class Pokemon {

    private int id;
    private int pokemonApiId;
    private String nombre;
    private String tipo;
    private String imagenUrl;
    private String apodo;
    private String notas;

    public Pokemon() {
    }

    public Pokemon(int pokemonApiId, String nombre, String tipo, String imagenUrl) {
        this.pokemonApiId = pokemonApiId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.imagenUrl = imagenUrl;
    }

    public Pokemon(int id, int pokemonApiId, String nombre, String tipo, String imagenUrl,
                   String apodo, String notas) {
        this.id = id;
        this.pokemonApiId = pokemonApiId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.imagenUrl = imagenUrl;
        this.apodo = apodo;
        this.notas = notas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPokemonApiId() {
        return pokemonApiId;
    }

    public void setPokemonApiId(int pokemonApiId) {
        this.pokemonApiId = pokemonApiId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getNombreMostrar() {
        if (apodo != null && !apodo.trim().isEmpty()) {
            return apodo.trim();
        }
        return capitalizar(nombre);
    }

    public static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}
