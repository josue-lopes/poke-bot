package com.josuelopes.bot.pokebot;

import com.josuelopes.bot.pokebot.services.BotService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App 
{
    public App() 
    {
    }
    
    public static void main(String[] args) 
    {
        SpringApplication.run(App.class, args);
        BotService service = new BotService();
        service.startBot();
    }
}