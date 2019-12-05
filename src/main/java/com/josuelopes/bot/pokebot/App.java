package com.josuelopes.bot.pokebot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public final class App 
{
    private App() 
    {
    }
    
    public static void main(String[] args) 
    {
        try(InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            Properties prop = new Properties();
            prop.load(input);

            String token = prop.getProperty("bot.secret");

            DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
            System.out.println(api.createBotInvite());
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
        }
    }
}