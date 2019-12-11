package com.josuelopes.bot.pokebot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatModel 
{
    @JsonProperty("base_stat")
    private int baseStat;

    public StatModel() {
    }

    public int getBaseStat() 
    {
        return baseStat;
    }

    public void setBaseStat(int baseStat) 
    {
        this.baseStat = baseStat;
    }
}