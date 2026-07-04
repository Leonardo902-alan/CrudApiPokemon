package com.estudiantes.crud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Detalle completo de un Pokémon obtenido desde la PokeAPI.
 */
public class PokemonDetalle {

    private int apiId;
    private String nombre;
    private String imagenUrl;
    private int altura;
    private int peso;
    private List<String> tipos;
    private List<String> habilidades;

    public PokemonDetalle() {
        tipos = new ArrayList<>();
        habilidades = new ArrayList<>();
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public List<String> getTipos() {
        return tipos;
    }

    public void setTipos(List<String> tipos) {
        this.tipos = tipos;
    }

    public List<String> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<String> habilidades) {
        this.habilidades = habilidades;
    }

    public String getNombreFormateado() {
        return Pokemon.capitalizar(nombre);
    }

    public String getTiposTexto() {
        if (tipos.isEmpty()) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tipos.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(Pokemon.capitalizar(tipos.get(i)));
        }
        return builder.toString();
    }

    public String getHabilidadesTexto() {
        if (habilidades.isEmpty()) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < habilidades.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(Pokemon.capitalizar(habilidades.get(i)));
        }
        return builder.toString();
    }

    public String getTipoPrincipal() {
        return tipos.isEmpty() ? "desconocido" : tipos.get(0);
    }
}
