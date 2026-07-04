package com.estudiantes.crud.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.estudiantes.crud.R;
import com.estudiantes.crud.database.DatabaseHelper;
import com.estudiantes.crud.model.Pokemon;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Permite editar el apodo y las notas de un Pokémon capturado.
 */
public class EditarPokemonActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextInputEditText etApodo;
    private TextInputEditText etNotas;
    private int pokemonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pokemon);

        databaseHelper = new DatabaseHelper(this);
        etApodo = findViewById(R.id.etApodo);
        etNotas = findViewById(R.id.etNotas);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        MaterialButton btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> actualizarPokemon());

        if (!obtenerIdPokemon()) {
            return;
        }

        cargarDatos();
    }

    private boolean obtenerIdPokemon() {
        pokemonId = getIntent().getIntExtra(MiPokedexActivity.EXTRA_POKEMON_ID, -1);

        if (pokemonId <= 0) {
            Toast.makeText(this, R.string.error_pokemon_local_no_encontrado, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }

    private void cargarDatos() {
        Pokemon pokemon = databaseHelper.buscarPokemonPorId(pokemonId);

        if (pokemon == null) {
            Toast.makeText(this, R.string.error_pokemon_local_no_encontrado, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView ivSprite = findViewById(R.id.ivSprite);
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvTipo = findViewById(R.id.tvTipo);

        tvNombre.setText(Pokemon.capitalizar(pokemon.getNombre()));
        tvTipo.setText(Pokemon.capitalizar(pokemon.getTipo()));

        Glide.with(this)
                .load(pokemon.getImagenUrl())
                .placeholder(R.drawable.ic_pokeball)
                .into(ivSprite);

        etApodo.setText(pokemon.getApodo());
        etNotas.setText(pokemon.getNotas());
    }

    private void actualizarPokemon() {
        Pokemon pokemon = databaseHelper.buscarPokemonPorId(pokemonId);

        if (pokemon == null) {
            Toast.makeText(this, R.string.error_pokemon_local_no_encontrado, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String apodo = etApodo.getText() != null ? etApodo.getText().toString().trim() : "";
        String notas = etNotas.getText() != null ? etNotas.getText().toString().trim() : "";

        pokemon.setApodo(apodo);
        pokemon.setNotas(notas);

        int filas = databaseHelper.actualizarPokemon(pokemon);

        if (filas > 0) {
            Toast.makeText(this, R.string.pokemon_actualizado, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, R.string.error_actualizar_pokemon, Toast.LENGTH_SHORT).show();
        }
    }
}
