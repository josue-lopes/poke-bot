package com.josuelopes.bot.pokebot.services;

import java.util.Arrays;
import java.util.Optional;

import com.josuelopes.bot.pokebot.debug.RestTemplateErrorHandler;
import com.josuelopes.bot.pokebot.models.PokeModel;

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
}