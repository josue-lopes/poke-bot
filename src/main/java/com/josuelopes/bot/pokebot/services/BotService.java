package com.josuelopes.bot.pokebot.services;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.josuelopes.bot.pokebot.models.PokeModel;
import com.josuelopes.bot.pokebot.models.SpriteModel;
import com.josuelopes.bot.pokebot.models.StatModel;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotService
{
    private final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

    private DiscordApi discordApi;
    private PokeApi pokeApi;

    public BotService() 
    { 
        pokeApi = new PokeApi();
    }

    public void init()
    {
        LOGGER.info("------Initializing PokeBot 1.0.0------");
        loginBot();
        setUpCommands();
    }

    // logs the bot into Discord with secret
    private void loginBot()
    {
        LOGGER.info("Logging into Discord with credentials from file");
        
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
            LOGGER.error("IOException in BotService class: Wasn't able to read Discord credentials from config.properties\n", exception);
        }
    }

    // sets up all commands the bot will be listening for
    private void setUpCommands()
    {
        LOGGER.info("Adding listener for !pokemon command");

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
        // first substring after !pokemon command identifies the unique pokemon either with ID or Name
        String[] splitCommand = message.split(" ");
        Optional<PokeModel> pokeData = pokeApi.getPokemon(splitCommand[1].toLowerCase());

        // if the API succesfully returned unique pokemon data 
        if (pokeData.isPresent())
        {
            PokeModel validPoke = pokeData.get();
            List<StatModel> stats = validPoke.getStats();
            SpriteModel sprites = validPoke.getSprites();

            // create message with Pokemon data
            // contents: name, id, stats, custom nature recommendation & embedded sprite image
            new MessageBuilder()
                .append("Name: ", MessageDecoration.BOLD) 
                .append(validPoke.getName() + "\n")
                .append("ID: ", MessageDecoration.BOLD)
                .append(validPoke.getId() + "\n")
                .append("Speed: ", MessageDecoration.BOLD)
                .append(stats.get(PokeApi.STAT_SPEED).getBaseStat() + "\n")
                .append("Special Defence: ", MessageDecoration.BOLD)
                .append(stats.get(PokeApi.STAT_SP_DEF).getBaseStat() + "\n")
                .append("Special Attack: ", MessageDecoration.BOLD)
                .append(stats.get(PokeApi.STAT_SP_ATK).getBaseStat() + "\n")
                .append("Defence: ", MessageDecoration.BOLD)
                .append(stats.get(PokeApi.STAT_DEF).getBaseStat() + "\n")
                .append("Attack: ", MessageDecoration.BOLD)
                .append(stats.get(PokeApi.STAT_ATK).getBaseStat() + "\n")
                .append("HP: ", MessageDecoration.BOLD)
                .append(stats.get(PokeApi.STAT_HP).getBaseStat() + "\n")
                .append("Base Stat Total: ", MessageDecoration.BOLD)
                .append(pokeApi.getStatTotal(stats))
                .append("\nOptimized Stat Total: ", MessageDecoration.BOLD)
                .append(pokeApi.getModifiedStatTotal(stats))
                .append("\n")
                .append(pokeApi.getNatureRecommendation(stats), MessageDecoration.BOLD)
                .setEmbed(new EmbedBuilder()
                    .setTitle(validPoke.getName())
                    .setColor(Color.RED)
                    .setImage(sprites.getFrontSprite()))
                .send(channel);

            LOGGER.info("Outputting info for " + validPoke.getName() + " to Discord");
        }
        else
        {
            LOGGER.warn("No Pokemon data was able to be returned from the last API call");
            channel.sendMessage("Pokemon wasn't found! Check if you're using the command correctly.");
        }
    }
}