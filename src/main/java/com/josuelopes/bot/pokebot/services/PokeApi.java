package com.josuelopes.bot.pokebot.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.josuelopes.bot.pokebot.models.PokeModel;
import com.josuelopes.bot.pokebot.models.StatModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class PokeApi
{
    // used to identify stat collection indices
    public static final int STAT_SPEED = 0;
    public static final int STAT_SP_DEF = 1;
    public static final int STAT_SP_ATK = 2;
    public static final int STAT_DEF = 3;
    public static final int STAT_ATK = 4;
    public static final int STAT_HP = 5;

    private final Logger LOGGER = LoggerFactory.getLogger(PokeApi.class);

    // used to make http requests to pokemon database
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private HttpEntity<String> entity;
    private String apiUrl = "https://pokeapi.co/api/v2/pokemon/";
    
    public PokeApi() 
    {
        headers = new HttpHeaders();    
        restTemplate = new RestTemplate();
    
        // set user-agent to not get blocked by Pokemon API
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "spring");
        
        entity = new HttpEntity<String>("parameters", headers);
    }

    // get unique pokemon JSON file from API and convert into object
    public Optional<PokeModel> getPokemon(String id)
    {
        // add unique ID to the base url
        String pokeUrl = apiUrl.concat(id);

        LOGGER.info("Attempting to grab pokemon data at " + pokeUrl);
        
        try
        {
            // create an HTTP GET request to the API, convert JSON into object and return
            ResponseEntity<PokeModel> responseEntity = restTemplate.exchange(pokeUrl, HttpMethod.GET, entity, PokeModel.class);
            Optional<PokeModel> pokeData = Optional.of(responseEntity.getBody());

            return pokeData;
        }
        catch (RestClientException exception)
        {
            LOGGER.error("RestClientException in PokeApi class: HTTP error when requesting data from Pokemon API\n", exception);
            return Optional.empty();
        }
    }

    // analyze base stats of pokemon and recommend natures accordingly
    public String getNatureRecommendation(List<StatModel> stats)
    {
        LOGGER.info("Calculating nature recommendation for current Pokemon");

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

        // compare stats to determine which natures to recommend
        if (highestStatIndex == STAT_ATK)
        {
            natureRec += getPrimaryNatureString(Nature.NAT_ADAMANT, STAT_ATK);

            if (spDef > def && spDef >= speed)
                natureRec += getSecondaryNatureString(Nature.NAT_CAREFUL);
            else if (def > spDef && def >= speed)
                natureRec += getSecondaryNatureString(Nature.NAT_IMPISH);
            else if (speed > def && speed > spDef)
                natureRec += getSecondaryNatureString(Nature.NAT_JOLLY);
            else if (def == spDef)
                natureRec += getSecondaryNatureString(Nature.NAT_CAREFUL, Nature.NAT_IMPISH);
        }
        else if (highestStatIndex == STAT_SP_ATK)
        {
            natureRec += getPrimaryNatureString(Nature.NAT_MODEST, STAT_SP_ATK);

            if (spDef > def && spDef >= speed)
                natureRec += getSecondaryNatureString(Nature.NAT_CALM);
            else if (def > spDef && def >= speed)
                natureRec += getSecondaryNatureString(Nature.NAT_BOLD);
            else if (speed > def && speed > spDef)
                natureRec += getSecondaryNatureString(Nature.NAT_TIMID);
            else if (def == spDef)
                natureRec += getSecondaryNatureString(Nature.NAT_CALM, Nature.NAT_BOLD);
        }
        else if (highestStatIndex == STAT_DEF)
        {
            if (spAtk > atk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_BOLD, STAT_DEF);

                if (spAtk >= speed)
                    natureRec += getSecondaryNatureString(Nature.NAT_MODEST);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_TIMID);
                
            }
            else if (atk > spAtk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_IMPISH, STAT_DEF);

                if (atk >= speed)
                    natureRec += getSecondaryNatureString(Nature.NAT_ADAMANT);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_JOLLY);
            }
            else if (atk == spAtk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_BOLD, Nature.NAT_IMPISH, STAT_DEF);

                if (atk >= speed)
                    natureRec += getSecondaryNatureString(Nature.NAT_ADAMANT, Nature.NAT_MODEST);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_JOLLY, Nature.NAT_TIMID); 
            }
        }
        else if (highestStatIndex == STAT_SP_DEF)
        {
            if (spAtk > atk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_CALM, STAT_SP_DEF);

                if (spAtk >= speed)
                    natureRec += getSecondaryNatureString(Nature.NAT_MODEST);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_TIMID);
            }
            else if (atk > spAtk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_CAREFUL, STAT_SP_DEF);

                if (atk >= speed)
                    natureRec += getSecondaryNatureString(Nature.NAT_ADAMANT);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_JOLLY);
            }
            else if (atk == spAtk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_CALM, Nature.NAT_CAREFUL, STAT_SP_DEF);

                if (atk >= speed)
                    natureRec += getSecondaryNatureString(Nature.NAT_ADAMANT, Nature.NAT_MODEST);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_JOLLY, Nature.NAT_TIMID);
            }
        }
        else if (highestStatIndex == STAT_SPEED)
        {
            if (spAtk > atk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_TIMID, STAT_SPEED);

                if (def > spAtk)
                    natureRec += getSecondaryNatureString(Nature.NAT_BOLD);
                else if (spDef > spAtk)
                    natureRec += getSecondaryNatureString(Nature.NAT_CALM);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_MODEST);

            }
            else if (atk > spAtk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_JOLLY, STAT_SPEED);

                if (def > atk)
                    natureRec += getSecondaryNatureString(Nature.NAT_IMPISH);
                else if (spDef > atk)
                    natureRec += getSecondaryNatureString(Nature.NAT_CAREFUL);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_ADAMANT);
            }
            else if (atk == spAtk)
            {
                natureRec += getPrimaryNatureString(Nature.NAT_TIMID, Nature.NAT_JOLLY, STAT_SPEED);

                if (def > atk)
                    natureRec += getSecondaryNatureString(Nature.NAT_BOLD, Nature.NAT_IMPISH);
                else if (spDef > atk)
                    natureRec += getSecondaryNatureString(Nature.NAT_CALM, Nature.NAT_CAREFUL);
                else
                    natureRec += getSecondaryNatureString(Nature.NAT_MODEST, Nature.NAT_ADAMANT);
            }
        }

        return natureRec;
    }

    // create formatted string for 1 primary nature recommendation
    private String getPrimaryNatureString(Nature nature, int highestStatIndex)
    {
        String natureString = "Highest Stat: " + getStringFromStat(highestStatIndex) + "\n";
        natureString += "Recommended Nature: " + getStringFromNature(nature) + "\n";
        
        return natureString;
    }

    // create formatted string for 2 primary natures recommendation
    private String getPrimaryNatureString(Nature nature1, Nature nature2, int highestStatIndex)
    {
        String natureString = "Highest Stat: " + getStringFromStat(highestStatIndex) + "\n";
        natureString += "Recommended Nature: " + getStringFromNature(nature1) + ", ";
        natureString += getStringFromNature(nature2) + "\n";
        
        return natureString;
    }

    // create formatted string for 1 secondary nature recommendation
    private String getSecondaryNatureString(Nature nature)
    {
        String natureString = "Secondary Option: " + getStringFromNature(nature) + "\n";
        
        return natureString;
    }

    // create formatted string for 2 secondary natures recommendation
    private String getSecondaryNatureString(Nature nature1, Nature nature2)
    {
        String natureString = "Secondary Option: " + getStringFromNature(nature1) + ", ";
        natureString += getStringFromNature(nature2) + "\n";
        
        return natureString;
    }

    // return formatted string based on input nature enum
    private String getStringFromNature(Nature nature)
    {
        switch(nature)
        {
            case NAT_ADAMANT:
                return "Adamant (+Atk/-Sp.Atk)";
            case NAT_BOLD:
                return "Bold (+Def/-Atk)";
            case NAT_CALM:
                return "Calm (+Sp.Def/-Atk)";
            case NAT_CAREFUL:
                return "Careful (+Sp.Def/-Sp.Atk)";
            case NAT_IMPISH:
                return "Impish (+Def/-Sp.Atk)";
            case NAT_JOLLY:
                return "Jolly (+Speed/-Sp.Atk)";
            case NAT_MODEST:
                return "Modest (+Sp.Atk/-Atk)";
            case NAT_TIMID:
                return "Timid (+Speed/-Atk)";
            default: 
                throw new IllegalArgumentException("Invalid nature argument\n");
        }
    }

    // return formatted string based on input stat constant
    private String getStringFromStat(int statIndex)
    {
        if (statIndex == STAT_SPEED)
            return "Speed";
        else if (statIndex == STAT_SP_DEF)
            return "Special Defence";
        else if (statIndex == STAT_SP_ATK)
            return "Special Attack";
        else if (statIndex == STAT_DEF)
            return "Defence";
        else if (statIndex == STAT_ATK)
            return "Attack";
        else
            throw new IllegalArgumentException("Invalid stat argument\n");
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

    private enum Nature
    {
        NAT_ADAMANT,
        NAT_MODEST,
        NAT_BOLD,
        NAT_IMPISH,
        NAT_CALM,
        NAT_CAREFUL,
        NAT_JOLLY,
        NAT_TIMID
    }
}