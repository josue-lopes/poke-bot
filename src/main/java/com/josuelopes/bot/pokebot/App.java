package com.josuelopes.bot.pokebot;

import com.josuelopes.bot.pokebot.services.BotService;

//TODO: migrate to Spring Boot
public final class App 
{
    private App() 
    {
    }
    
    public static void main(String[] args) 
    {
        BotService service = new BotService();
        service.startBot();
    }
}