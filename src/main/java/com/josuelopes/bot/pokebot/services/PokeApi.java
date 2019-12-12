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
        // store stat values to compare
        int speed = stats.get(STAT_SPEED).getBaseStat();
        int atk = stats.get(STAT_ATK).getBaseStat();
        int def = stats.get(STAT_DEF).getBaseStat();
        int spAtk = stats.get(STAT_SP_ATK).getBaseStat();
        int spDef = stats.get(STAT_SP_DEF).getBaseStat();

        // string to be constructed and returned
        String natureRec = "";

        // get index and value of highest stat
        int highestStatIndex = 0;
        int highestStatValue = 0;
        double statAverage = getAverageStatValue(stats);

        for (int i = 0; i < 5; ++i)
        {
            int baseStat = stats.get(i).getBaseStat();

            if (baseStat > highestStatValue && baseStat > statAverage)
            {
                highestStatValue = baseStat;
                highestStatIndex = i;
            }
        }

        if (highestStatIndex == STAT_ATK)
        {
            natureRec +="Highest Stat: Attack\nRecommended Nature: Adamant (+Atk/-Sp.Atk)\n";

            if (spDef > def && spDef >= speed)
                natureRec += "Secondary Option: Careful (+Sp.Def/-Sp.Atk)\n";
            else if (def > spDef && def >= speed)
                natureRec += "Secondary Option: Impish (+Def/-Sp.Atk)\n";
            else if (speed > def && speed > spDef)
                natureRec += "Secondary Option: Jolly (+Speed/-Sp.Atk)\n";
            else if (def == spDef)
                natureRec += "Secondary Option: Careful (+Sp.Def/-Sp.Atk), Impish (+Def/-Sp.Atk)\n";
        }
        else if (highestStatIndex == STAT_SP_ATK)
        {
            natureRec += "Highest Stat: Special Attack\nRecommended Nature: Modest (+Sp.Atk/-Atk)\n";

            if (spDef > def && spDef >= speed)
                natureRec += "Secondary Option: Calm (+Sp.Def/-Atk)\n";
            else if (def > spDef && def >= speed)
                natureRec += "Secondary Option: Bold (+Def/-Atk)\n";
            else if (speed > def && speed > spDef)
                natureRec += "Secondary Option: Timid (+Speed/-Atk)\n";
            else if (def == spDef)
                natureRec += "Secondary Option: Calm (+Sp.Def/-Atk), Bold (+Def/-Atk)\n";
        }
        else if (highestStatIndex == STAT_DEF)
        {
            if (spAtk > atk)
            {
                natureRec += "Highest Stat: Defence\nRecommended Nature: Bold (+Def/-Atk)\n";

                if (spAtk >= speed)
                    natureRec += "Secondary Option: Modest (+Sp.Atk/-Atk)\n";
                else
                    natureRec += "Secondary Option: Timid (+Speed/-Atk)\n";
                
            }
            else if (atk > spAtk)
            {
                natureRec += "Highest Stat: Defence\nRecommended Nature: Impish (+Def/-Sp.Atk)\n";

                if (atk >= speed)
                    natureRec += "Secondary Option: Adamant (+Atk/-Sp.Atk)\n";
                else
                    natureRec += "Secondary Option: Jolly (+Speed/-Sp.Atk)\n";
            }
            else if (atk == spAtk)
            {
                natureRec += "Highest Stat: Defence\nRecommended Nature: Bold (+Def/-Atk), Impish (+Def/-Sp.Atk)\n";

                if (atk >= speed)
                    natureRec += "Secondary Option: Adamant (+Atk/-Sp.Atk), Modest (+Sp.Atk/-Atk)\n";
                else
                    natureRec += "Secondary Option: Jolly (+Speed/-Sp.Atk), Timid (+Speed/-Atk)\n"; 
            }
        }
        else if (highestStatIndex == STAT_SP_DEF)
        {
            if (spAtk > atk)
            {
                natureRec += "Highest Stat: Special Defence\nRecommended Nature: Calm (+Sp.Def/-Atk)\n";

                if (spAtk >= speed)
                    natureRec += "Secondary Option: Modest (+Sp.Atk/-Atk)\n";
                else
                    natureRec += "Secondary Option: Timid (+Speed/-Atk)\n";
            }
            else if (atk > spAtk)
            {
                natureRec += "Highest Stat: Special Defence\nRecommended Nature: Careful (+Sp.Def/-Sp.Atk)\n";

                if (atk >= speed)
                    natureRec += "Secondary Option: Adamant (+Atk/-Sp.Atk)\n";
                else
                    natureRec += "Secondary Option: Jolly (+Speed/-Sp.Atk)\n";
            }
            else if (atk == spAtk)
            {
                natureRec += "Highest Stat: Special Defence\nRecommended Nature: Calm (+Sp.Def/-Atk), Careful (+Sp.Def/-Sp.Atk)\n";

                if (atk >= speed)
                    natureRec += "Secondary Option: Adamant (+Atk/-Sp.Atk), Modest (+Sp.Atk/-Atk)\n";
                else
                    natureRec += "Secondary Option: Jolly (+Speed/-Sp.Atk), Timid (+Speed/-Atk)\n";
            }
        }
        else if (highestStatIndex == STAT_SPEED)
        {
            if (spAtk > atk)
            {
                natureRec += "Highest Stat: Speed\nRecommended Nature: Timid (+Speed/-Atk)\n";

                if (def > spAtk)
                    natureRec += "Secondary Option: Bold (+Def/-Atk)\n";
                else if (spDef > spAtk)
                    natureRec += "Secondary Option: Calm (+Sp.Def/-Atk)\n";
                else
                    natureRec += "Secondary Option: Modest (+Sp.Atk/-Atk)\n";

            }
            else if (atk > spAtk)
            {
                natureRec += "Highest Stat: Speed\nRecommended Nature: Jolly (+Speed/-Sp.Atk)\n";

                if (def > atk)
                    natureRec += "Secondary Option: Impish (+Def/-Sp.Atk)\n";
                else if (spDef > atk)
                    natureRec += "Secondary Option: Careful (+Sp.Def/-Sp.Atk)\n";
                else
                    natureRec += "Secondary Option: Adamant (+Atk/-Sp.Atk)\n";
            }
            else if (atk == spAtk)
            {
                natureRec += "Highest Stat: Speed\nRecommended Nature: Timid (+Speed/-Atk), Jolly (+Speed/-Sp.Atk)\n";

                if (def > atk)
                    natureRec += "Secondary Option: Bold (+Def/-Atk), Impish (+Def/-Sp.Atk)\n";
                else if (spDef > atk)
                    natureRec += "Secondary Option: Careful (+Sp.Def/-Sp.Atk), Calm (+Sp.Def/-Atk)\n";
                else
                    natureRec += "Secondary Option: Modest (+Sp.Atk/-Atk), Adamant (+Atk/-Sp.Atk)\n";
            }
        }

        return natureRec;
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