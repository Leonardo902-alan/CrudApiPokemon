package com.estudiantes.crud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.estudiantes.crud.R;

/**
 * Menú principal de la aplicación Pokémon.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        View cardExplorar = findViewById(R.id.cardExplorar);
        View cardPokedex = findViewById(R.id.cardPokedex);

        cardExplorar.setOnClickListener(v ->
                startActivity(new Intent(this, ExplorarActivity.class)));

        cardPokedex.setOnClickListener(v ->
                startActivity(new Intent(this, MiPokedexActivity.class)));
    }
}
