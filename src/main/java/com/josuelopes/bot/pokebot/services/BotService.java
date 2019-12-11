package com.josuelopes.bot.pokebot.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.josuelopes.bot.pokebot.models.PokeModel;
import com.josuelopes.bot.pokebot.models.StatModel;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

public class BotService
{
    private DiscordApi discordApi;
    private PokeApi pokeApi;

    public BotService() 
    { 
        pokeApi = new PokeApi();
    }

    public void init()
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
                getPokemonCommand(event.getChannel(), message);
            }
        });
    }

    // Gets proper pokemon info using API, formats and sends message
    private void getPokemonCommand(TextChannel channel, String message)
    {
        String[] splitCommand = message.split(" ");
        Optional<PokeModel> pokeData = pokeApi.getPokemon(splitCommand[1]);

        if (pokeData.isPresent())
        {
            PokeModel validPoke = pokeData.get();
            List<StatModel> stats = validPoke.getStats();

            channel.sendMessage(
                validPoke.getName() + " " + 
                stats.get(0).getBaseStat() + " " +
                stats.get(1).getBaseStat() + " " +
                stats.get(2).getBaseStat() + " " +
                stats.get(3).getBaseStat() + " " +
                stats.get(4).getBaseStat() + " " +
                stats.get(5).getBaseStat() + " "
            );
        }
        else
        {
            channel.sendMessage("Pokemon wasn't found! Check if you're using the command correctly.");
        }
    }
}