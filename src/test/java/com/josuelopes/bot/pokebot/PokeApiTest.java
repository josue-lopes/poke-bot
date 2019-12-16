package com.josuelopes.bot.pokebot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.josuelopes.bot.pokebot.models.PokeModel;
import com.josuelopes.bot.pokebot.services.PokeApi;

import org.junit.BeforeClass;
import org.junit.Test;

public class PokeApiTest
{
    private static PokeApi api;

    @BeforeClass
    public static void init()
    {
        api = new PokeApi();
    }

    @Test
    public void isPokemonValidWithIndex()
    {
        Optional<PokeModel> poke = api.getPokemon("1");

        assertTrue("Pokemon data is not able to be retrieved by index.", poke.get().getId() == 1);
    }

    @Test
    public void isPokemonValidWithName()
    {
        Optional<PokeModel> poke = api.getPokemon("bulbasaur");

        assertTrue("Pokemon data is not able to be retrieved by name.", poke.get().getId() == 1);
    }

    @Test
    public void isInvalidPokemonHandled()
    {
        Optional<PokeModel> poke = api.getPokemon("-1");
        assertFalse("Invalid Pokemon input exception not handled.", poke.isPresent());
    }
}