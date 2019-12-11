package com.josuelopes.bot.pokebot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpriteModel
{
    @JsonProperty("back_default")
    private String backSprite;
    @JsonProperty("front_default")
    private String frontSprite;
    
    public SpriteModel()
    {

    }

    public String getBackSprite() 
    {
        return backSprite;
    }

    public void setBackSprite(String backSprite) 
    {
        this.backSprite = backSprite;
    }

    public String getFrontSprite() 
    {
        return frontSprite;
    }

    public void setFrontSprite(String frontSprite) 
    {
        this.frontSprite = frontSprite;
    }
}