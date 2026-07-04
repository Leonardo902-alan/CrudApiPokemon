package com.estudiantes.crud.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.estudiantes.crud.R;
import com.estudiantes.crud.api.PokeApiClient;
import com.estudiantes.crud.database.DatabaseHelper;
import com.estudiantes.crud.model.Pokemon;
import com.estudiantes.crud.model.PokemonDetalle;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Muestra el detalle de un Pokémon desde la PokeAPI y permite capturarlo en SQLite.
 */
public class DetallePokemonActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ProgressBar progressBar;
    private View contenidoDetalle;
    private MaterialButton btnCapturar;

    private PokemonDetalle pokemonDetalle;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pokemon);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progressBar);
        contenidoDetalle = findViewById(R.id.contenidoDetalle);
        btnCapturar = findViewById(R.id.btnCapturar);
        btnCapturar.setOnClickListener(v -> capturarPokemon());

        cargarDetalle();
    }

    private void cargarDetalle() {
        String nombre = getIntent().getStringExtra(ExplorarActivity.EXTRA_POKEMON_NOMBRE);
        if (nombre == null || nombre.isEmpty()) {
            Toast.makeText(this, R.string.error_pokemon_no_encontrado, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        contenidoDetalle.setVisibility(View.GONE);

        executor.execute(() -> {
            try {
                PokemonDetalle detalle = PokeApiClient.obtenerDetallePokemon(nombre);

                runOnUiThread(() -> {
                    pokemonDetalle = detalle;
                    mostrarDetalle(detalle);
                    progressBar.setVisibility(View.GONE);
                    contenidoDetalle.setVisibility(View.VISIBLE);
                    actualizarBotonCapturar();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.error_api, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void mostrarDetalle(PokemonDetalle detalle) {
        ImageView ivSprite = findViewById(R.id.ivSprite);
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvNumero = findViewById(R.id.tvNumero);
        TextView tvTipos = findViewById(R.id.tvTipos);
        TextView tvAltura = findViewById(R.id.tvAltura);
        TextView tvPeso = findViewById(R.id.tvPeso);
        TextView tvHabilidades = findViewById(R.id.tvHabilidades);

        tvNombre.setText(detalle.getNombreFormateado());
        tvNumero.setText(getString(R.string.numero_pokemon, detalle.getApiId()));
        tvTipos.setText(detalle.getTiposTexto());
        tvAltura.setText(getString(R.string.altura_label, detalle.getAltura() / 10.0));
        tvPeso.setText(getString(R.string.peso_label, detalle.getPeso() / 10.0));
        tvHabilidades.setText(getString(R.string.habilidades_label, detalle.getHabilidadesTexto()));

        Glide.with(this)
                .load(detalle.getImagenUrl())
                .placeholder(R.drawable.ic_pokeball)
                .into(ivSprite);
    }

    private void actualizarBotonCapturar() {
        if (pokemonDetalle == null) {
            return;
        }

        boolean capturado = databaseHelper.existePokemonCapturado(pokemonDetalle.getApiId());
        btnCapturar.setEnabled(!capturado);
        btnCapturar.setText(capturado ? R.string.ya_capturado : R.string.capturar_pokemon);
    }

    private void capturarPokemon() {
        if (pokemonDetalle == null) {
            return;
        }

        if (databaseHelper.existePokemonCapturado(pokemonDetalle.getApiId())) {
            Toast.makeText(this, R.string.error_duplicado_pokemon, Toast.LENGTH_SHORT).show();
            return;
        }

        Pokemon pokemon = new Pokemon(
                pokemonDetalle.getApiId(),
                pokemonDetalle.getNombre(),
                pokemonDetalle.getTipoPrincipal(),
                pokemonDetalle.getImagenUrl()
        );

        long id = databaseHelper.insertarPokemon(pokemon);

        if (id > 0) {
            Toast.makeText(this, R.string.pokemon_capturado, Toast.LENGTH_SHORT).show();
            actualizarBotonCapturar();
        } else {
            Toast.makeText(this, R.string.error_guardar_pokemon, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
