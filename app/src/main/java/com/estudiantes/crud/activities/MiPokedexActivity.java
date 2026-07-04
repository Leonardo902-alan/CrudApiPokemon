package com.estudiantes.crud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.estudiantes.crud.R;
import com.estudiantes.crud.adapter.PokemonLocalAdapter;
import com.estudiantes.crud.database.DatabaseHelper;
import com.estudiantes.crud.model.Pokemon;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Pokédex personal con CRUD local en SQLite.
 */
public class MiPokedexActivity extends AppCompatActivity implements PokemonLocalAdapter.OnPokemonActionListener {

    public static final String EXTRA_POKEMON_ID = "pokemon_local_id";

    private DatabaseHelper databaseHelper;
    private PokemonLocalAdapter adapter;
    private TextView tvListaVacia;

    private final ActivityResultLauncher<Intent> editarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarPokemon();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_pokedex);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvListaVacia = findViewById(R.id.tvListaVacia);
        FloatingActionButton fabExplorar = findViewById(R.id.fabExplorar);
        fabExplorar.setOnClickListener(v ->
                startActivity(new Intent(this, ExplorarActivity.class)));

        configurarRecyclerView();
    }

    private void configurarRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerPokemon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PokemonLocalAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void cargarPokemon() {
        List<Pokemon> pokemonList = databaseHelper.obtenerTodosPokemon();
        adapter.actualizarLista(pokemonList);
        tvListaVacia.setVisibility(pokemonList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEditarClick(Pokemon pokemon) {
        Intent intent = new Intent(this, EditarPokemonActivity.class);
        intent.putExtra(EXTRA_POKEMON_ID, pokemon.getId());
        editarLauncher.launch(intent);
    }

    @Override
    public void onEliminarClick(Pokemon pokemon) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_titulo)
                .setMessage(R.string.eliminar_mensaje_pokemon)
                .setPositiveButton(R.string.si, (dialog, which) -> eliminarPokemon(pokemon))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void eliminarPokemon(Pokemon pokemon) {
        int filas = databaseHelper.eliminarPokemon(pokemon.getId());

        if (filas > 0) {
            Toast.makeText(this, R.string.pokemon_eliminado, Toast.LENGTH_SHORT).show();
            cargarPokemon();
        } else {
            Toast.makeText(this, R.string.error_eliminar_pokemon, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPokemon();
    }
}
