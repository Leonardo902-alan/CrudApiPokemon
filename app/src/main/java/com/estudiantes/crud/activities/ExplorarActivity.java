package com.estudiantes.crud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.estudiantes.crud.R;
import com.estudiantes.crud.adapter.PokemonApiAdapter;
import com.estudiantes.crud.api.PokeApiClient;
import com.estudiantes.crud.model.PokemonResumen;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Explora Pokémon desde la PokeAPI con paginación y búsqueda por nombre.
 */
public class ExplorarActivity extends AppCompatActivity implements PokemonApiAdapter.OnPokemonClickListener {

    public static final String EXTRA_POKEMON_NOMBRE = "pokemon_nombre";
    public static final String EXTRA_POKEMON_API_ID = "pokemon_api_id";

    private static final int LIMITE_POR_PAGINA = 20;

    private PokemonApiAdapter adapter;
    private TextView tvListaVacia;
    private ProgressBar progressBar;
    private MaterialButton btnCargarMas;
    private TextInputEditText etBuscar;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int offsetActual = 0;
    private boolean cargando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorar);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvListaVacia = findViewById(R.id.tvListaVacia);
        progressBar = findViewById(R.id.progressBar);
        btnCargarMas = findViewById(R.id.btnCargarMas);
        etBuscar = findViewById(R.id.etBuscar);

        configurarRecyclerView();
        btnCargarMas.setOnClickListener(v -> cargarMasPokemon());
        findViewById(R.id.btnBuscar).setOnClickListener(v -> buscarPokemon());

        cargarPokemonDesdeApi(true);
    }

    private void configurarRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerPokemon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PokemonApiAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void cargarPokemonDesdeApi(boolean reiniciar) {
        if (cargando) {
            return;
        }

        if (reiniciar) {
            offsetActual = 0;
        }

        cargando = true;
        mostrarCarga(true);

        executor.execute(() -> {
            try {
                List<PokemonResumen> pokemon = PokeApiClient.obtenerListaPokemon(offsetActual, LIMITE_POR_PAGINA);
                offsetActual += LIMITE_POR_PAGINA;

                runOnUiThread(() -> {
                    if (reiniciar) {
                        adapter.actualizarLista(pokemon);
                    } else {
                        adapter.agregarPokemon(pokemon);
                    }
                    actualizarEstadoVacio(adapter.getItemCount() == 0);
                    btnCargarMas.setEnabled(!pokemon.isEmpty());
                    finalizarCarga();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.error_api, Toast.LENGTH_SHORT).show();
                    finalizarCarga();
                });
            }
        });
    }

    private void buscarPokemon() {
        String nombre = etBuscar.getText() != null ? etBuscar.getText().toString().trim() : "";

        if (nombre.isEmpty()) {
            cargarPokemonDesdeApi(true);
            return;
        }

        if (cargando) {
            return;
        }

        cargando = true;
        mostrarCarga(true);
        btnCargarMas.setEnabled(false);

        executor.execute(() -> {
            try {
                com.estudiantes.crud.model.PokemonDetalle detalle =
                        PokeApiClient.obtenerDetallePokemon(nombre);
                PokemonResumen resumen = new PokemonResumen(
                        detalle.getApiId(),
                        detalle.getNombre(),
                        detalle.getImagenUrl()
                );
                List<PokemonResumen> lista = new ArrayList<>();
                lista.add(resumen);

                runOnUiThread(() -> {
                    adapter.actualizarLista(lista);
                    actualizarEstadoVacio(false);
                    finalizarCarga();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    adapter.actualizarLista(new ArrayList<>());
                    actualizarEstadoVacio(true);
                    Toast.makeText(this, R.string.error_pokemon_no_encontrado, Toast.LENGTH_SHORT).show();
                    finalizarCarga();
                });
            }
        });
    }

    private void cargarMasPokemon() {
        cargarPokemonDesdeApi(false);
    }

    private void actualizarEstadoVacio(boolean vacio) {
        tvListaVacia.setVisibility(vacio ? View.VISIBLE : View.GONE);
    }

    private void mostrarCarga(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void finalizarCarga() {
        cargando = false;
        mostrarCarga(false);
    }

    @Override
    public void onPokemonClick(PokemonResumen pokemon) {
        Intent intent = new Intent(this, DetallePokemonActivity.class);
        intent.putExtra(EXTRA_POKEMON_NOMBRE, pokemon.getNombre());
        intent.putExtra(EXTRA_POKEMON_API_ID, pokemon.getApiId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
