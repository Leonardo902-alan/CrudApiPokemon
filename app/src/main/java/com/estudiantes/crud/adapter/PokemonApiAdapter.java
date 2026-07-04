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
import com.estudiantes.crud.model.PokemonResumen;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar Pokémon obtenidos desde la PokeAPI.
 */
public class PokemonApiAdapter extends RecyclerView.Adapter<PokemonApiAdapter.PokemonViewHolder> {

    public interface OnPokemonClickListener {
        void onPokemonClick(PokemonResumen pokemon);
    }

    private List<PokemonResumen> pokemonList;
    private final OnPokemonClickListener listener;

    public PokemonApiAdapter(OnPokemonClickListener listener) {
        this.pokemonList = new ArrayList<>();
        this.listener = listener;
    }

    public void actualizarLista(List<PokemonResumen> nuevaLista) {
        this.pokemonList = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void agregarPokemon(List<PokemonResumen> masPokemon) {
        if (masPokemon != null && !masPokemon.isEmpty()) {
            int inicio = pokemonList.size();
            pokemonList.addAll(masPokemon);
            notifyItemRangeInserted(inicio, masPokemon.size());
        }
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon_api, parent, false);
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
        private final TextView tvNumero;

        PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSprite = itemView.findViewById(R.id.ivSprite);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvNumero = itemView.findViewById(R.id.tvNumero);
        }

        void bind(final PokemonResumen pokemon) {
            tvNombre.setText(pokemon.getNombreFormateado());
            tvNumero.setText(itemView.getContext().getString(R.string.numero_pokemon, pokemon.getApiId()));

            Glide.with(itemView.getContext())
                    .load(pokemon.getImagenUrl())
                    .placeholder(R.drawable.ic_pokeball)
                    .into(ivSprite);

            itemView.setOnClickListener(v -> listener.onPokemonClick(pokemon));
        }
    }
}
