package com.estudiantes.crud.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.estudiantes.crud.R;
import com.estudiantes.crud.model.Pokemon;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para la Pokédex personal almacenada en SQLite.
 */
public class PokemonLocalAdapter extends RecyclerView.Adapter<PokemonLocalAdapter.PokemonViewHolder> {

    public interface OnPokemonActionListener {
        void onEditarClick(Pokemon pokemon);

        void onEliminarClick(Pokemon pokemon);
    }

    private List<Pokemon> pokemonList;
    private final OnPokemonActionListener listener;

    public PokemonLocalAdapter(OnPokemonActionListener listener) {
        this.pokemonList = new ArrayList<>();
        this.listener = listener;
    }

    public void actualizarLista(List<Pokemon> nuevaLista) {
        this.pokemonList = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon_local, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        holder.bind(pokemonList.get(position));
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    class PokemonViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivSprite;
        private final TextView tvNombre;
        private final TextView tvTipo;
        private final TextView tvApodo;
        private final MaterialButton btnEditar;
        private final MaterialButton btnEliminar;

        PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSprite = itemView.findViewById(R.id.ivSprite);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvApodo = itemView.findViewById(R.id.tvApodo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        void bind(final Pokemon pokemon) {
            tvNombre.setText(Pokemon.capitalizar(pokemon.getNombre()));
            tvTipo.setText(Pokemon.capitalizar(pokemon.getTipo()));

            if (pokemon.getApodo() != null && !pokemon.getApodo().trim().isEmpty()) {
                tvApodo.setVisibility(View.VISIBLE);
                tvApodo.setText(pokemon.getApodo().trim());
            } else {
                tvApodo.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(pokemon.getImagenUrl())
                    .placeholder(R.drawable.ic_pokeball)
                    .into(ivSprite);

            btnEditar.setOnClickListener(v -> listener.onEditarClick(pokemon));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(pokemon));
        }
    }
}
