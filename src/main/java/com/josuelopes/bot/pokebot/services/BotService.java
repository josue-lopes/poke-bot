package com.josuelopes.bot.pokebot.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

public class BotService
{
    private DiscordApi discordApi;

    public BotService(){   }

    // must be called to use bot
    public void startBot()
    {
        loginBot();
        setUpCommands();
    }

    // logs the bot into Discord with secret
    private void loginBot()
    {
        try(InputStream input = BotService.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            Properties prop = new Properties();
            prop.load(input);

            // login to Discord with secret from config file
            String token = prop.getProperty("bot.secret");
            discordApi = new DiscordApiBuilder().setToken(token).login().join();
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
        }
    }

    // sets up all commands the bot will be listening for
    private void setUpCommands()
    {
        // !pokemon command
        discordApi.addMessageCreateListener(event -> {
            String message = event.getMessageContent();
            if (message.startsWith("!pokemon")) 
            {
                event.getChannel().sendMessage("Grabbing Pokemon info...");
                getPokemonCommand(event.getChannel(), message);
            }
        });
    }

    // Gets proper pokemon info using API, formats and sends message
    private void getPokemonCommand(TextChannel channel, String message)
    {

    }
}