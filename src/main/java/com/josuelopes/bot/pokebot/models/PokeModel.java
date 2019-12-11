package com.josuelopes.bot.pokebot.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PokeModel 
{
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private int id;
    @JsonProperty("stats")
    private List<StatModel> stats;
    @JsonProperty("sprites")
    private SpriteModel sprites;

    public PokeModel() 
    {
        stats = new ArrayList<StatModel>();
        sprites = new SpriteModel();
    }

    public SpriteModel getSprites() 
    {
        return sprites;
    }

    public void setSprites(SpriteModel sprites) 
    {
        this.sprites = sprites;
    }

    public List<StatModel> getStats() 
    {
        return stats;
    }

    public void setStats(List<StatModel> stats) 
    {
        this.stats = stats;
    }

    public int getId() 
    {
        return id;
    }

    public void setId(int id) 
    {
        this.id = id;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }
}