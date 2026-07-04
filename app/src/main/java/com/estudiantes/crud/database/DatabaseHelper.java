package com.estudiantes.crud.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.estudiantes.crud.model.Pokemon;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la base de datos SQLite local para la Pokédex personal del usuario.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pokedex_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_POKEMON = "mis_pokemon";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_API_ID = "pokemon_api_id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_TIPO = "tipo";
    private static final String COLUMN_IMAGEN = "imagen_url";
    private static final String COLUMN_APODO = "apodo";
    private static final String COLUMN_NOTAS = "notas";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_POKEMON + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_API_ID + " INTEGER NOT NULL UNIQUE, "
                + COLUMN_NOMBRE + " TEXT NOT NULL, "
                + COLUMN_TIPO + " TEXT NOT NULL, "
                + COLUMN_IMAGEN + " TEXT NOT NULL, "
                + COLUMN_APODO + " TEXT, "
                + COLUMN_NOTAS + " TEXT"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POKEMON);
        onCreate(db);
    }

    /**
     * Inserta un Pokémon capturado en la Pokédex local.
     */
    public long insertarPokemon(Pokemon pokemon) {
        SQLiteDatabase db = this.getWritableDatabase();
        long idInsertado = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_API_ID, pokemon.getPokemonApiId());
            values.put(COLUMN_NOMBRE, pokemon.getNombre().trim());
            values.put(COLUMN_TIPO, pokemon.getTipo().trim());
            values.put(COLUMN_IMAGEN, pokemon.getImagenUrl());
            values.put(COLUMN_APODO, pokemon.getApodo() != null ? pokemon.getApodo().trim() : "");
            values.put(COLUMN_NOTAS, pokemon.getNotas() != null ? pokemon.getNotas().trim() : "");

            idInsertado = db.insert(TABLE_POKEMON, null, values);
        } catch (Exception e) {
            idInsertado = -1;
        } finally {
            db.close();
        }

        return idInsertado;
    }

    /**
     * Obtiene todos los Pokémon guardados localmente.
     */
    public List<Pokemon> obtenerTodosPokemon() {
        List<Pokemon> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_POKEMON,
                    null,
                    null,
                    null,
                    null,
                    null,
                    COLUMN_NOMBRE + " ASC"
            );

            if (cursor.moveToFirst()) {
                do {
                    lista.add(cursorToPokemon(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return lista;
    }

    /**
     * Busca un Pokémon local por su ID de SQLite.
     */
    public Pokemon buscarPokemonPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Pokemon pokemon = null;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_POKEMON,
                    null,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                pokemon = cursorToPokemon(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return pokemon;
    }

    /**
     * Actualiza apodo y notas de un Pokémon capturado.
     */
    public int actualizarPokemon(Pokemon pokemon) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasAfectadas = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_APODO, pokemon.getApodo() != null ? pokemon.getApodo().trim() : "");
            values.put(COLUMN_NOTAS, pokemon.getNotas() != null ? pokemon.getNotas().trim() : "");

            filasAfectadas = db.update(
                    TABLE_POKEMON,
                    values,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(pokemon.getId())}
            );
        } catch (Exception e) {
            filasAfectadas = 0;
        } finally {
            db.close();
        }

        return filasAfectadas;
    }

    /**
     * Elimina un Pokémon de la Pokédex local.
     */
    public int eliminarPokemon(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasEliminadas = 0;

        try {
            filasEliminadas = db.delete(
                    TABLE_POKEMON,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
        } catch (Exception e) {
            filasEliminadas = 0;
        } finally {
            db.close();
        }

        return filasEliminadas;
    }

    /**
     * Verifica si un Pokémon ya fue capturado (por ID de la PokeAPI).
     */
    public boolean existePokemonCapturado(int pokemonApiId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean existe = false;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_POKEMON,
                    new String[]{COLUMN_ID},
                    COLUMN_API_ID + " = ?",
                    new String[]{String.valueOf(pokemonApiId)},
                    null, null, null
            );
            existe = cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return existe;
    }

    private Pokemon cursorToPokemon(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        int apiId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_API_ID));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
        String tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO));
        String imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN));
        String apodo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APODO));
        String notas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS));

        return new Pokemon(id, apiId, nombre, tipo, imagen, apodo, notas);
    }
}
