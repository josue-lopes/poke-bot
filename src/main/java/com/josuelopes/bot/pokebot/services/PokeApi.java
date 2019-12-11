package com.josuelopes.bot.pokebot.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.josuelopes.bot.pokebot.debug.RestTemplateErrorHandler;
import com.josuelopes.bot.pokebot.models.PokeModel;
import com.josuelopes.bot.pokebot.models.StatModel;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class PokeApi
{
    public static final int STAT_SPEED = 0;
    public static final int STAT_SP_DEF = 1;
    public static final int STAT_SP_ATK = 2;
    public static final int STAT_DEF = 3;
    public static final int STAT_ATK = 4;
    public static final int STAT_HP = 5;

    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private HttpEntity<String> entity;

    private String apiUrl = "https://pokeapi.co/api/v2/pokemon/";
    
    public PokeApi() 
    {
        RestTemplateErrorHandler errorHandler = new RestTemplateErrorHandler();
        headers = new HttpHeaders();    
        restTemplate = new RestTemplate();
    
        // set headers for REST API
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "spring");
        entity = new HttpEntity<String>("parameters", headers);

        // set custom error handler
        restTemplate.setErrorHandler(errorHandler);
    }

    public Optional<PokeModel> getPokemon(String id)
    {
        String pokeUrl = apiUrl.concat(id);
        System.out.println(pokeUrl);
        
        try
        {
            ResponseEntity<PokeModel> responseEntity = restTemplate.exchange(pokeUrl, HttpMethod.GET, entity, PokeModel.class);
            Optional<PokeModel> pokeData = Optional.of(responseEntity.getBody());
            return pokeData;
        }
        catch (NullPointerException exception)
        {
            System.out.println("Null Pointer Exception: " + exception.getMessage());
            return Optional.empty();
        }
        catch (HttpClientErrorException exception)
        {
            System.out.println("HTTP Client Error Exception: " + exception.getMessage());
            return Optional.empty();
        }
        catch (HttpServerErrorException exception)
        {
            System.out.println("HTTP Server Error Exception: " + exception.getMessage());
            return Optional.empty();
        }
        catch (RestClientException exception)
        {
            System.out.println("Rest Client Exception: " + exception.getMessage());
            return Optional.empty();
        }
    }

    // analyze base stats of pokemon and recommend natures accordingly
    public String getNatureRecommendation(List<StatModel> stats)
    {
        String attackNature = "";
        String defenceNature = "";
        String speedNature = "";

        // compare attack stats
        if (stats.get(STAT_SP_ATK).getBaseStat() > stats.get(STAT_ATK).getBaseStat())
            attackNature = "Modest";
        else if (stats.get(STAT_ATK).getBaseStat() > stats.get(STAT_SP_ATK).getBaseStat())
            attackNature = "Adamant";

        // compare defence stats
        if (stats.get(STAT_SP_DEF).getBaseStat() > stats.get(STAT_DEF).getBaseStat())
        {
            if (attackNature == "Adamant")
                defenceNature = "Careful";
            else if (attackNature == "Modest")
                defenceNature = "Calm";
        }
        else if (stats.get(STAT_DEF).getBaseStat() > stats.get(STAT_SP_DEF).getBaseStat())
        {
            if (attackNature == "Adamant")
                defenceNature = "Impish";
            else if (attackNature == "Modest")
                defenceNature = "Bold";
        }

        int highestStatIndex = 0;
        int highestStatValue = 0;
        double statAverage = getAverageStatValue(stats);

        // loop through all stats except HP
        for (int i = 0; i < 5; ++i)
        {
            int baseStat = stats.get(i).getBaseStat();

            if (baseStat > highestStatValue && baseStat > statAverage)
            {
                highestStatValue = baseStat;
                highestStatIndex = i;
            }
        }

        if (highestStatValue == STAT_SPEED)
        {
            //if ("")
        }

        // TODO: check for even stat case
        // TODO: construct final string with recommended natures
        return "";
    }

    private double getAverageStatValue(List<StatModel> stats)
    {
        double average = 0.0;

        for(StatModel stat: stats)
        {
            average += stat.getBaseStat();
        }

        return (average / 6.0);
    }
}