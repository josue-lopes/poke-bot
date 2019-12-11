package com.josuelopes.bot.pokebot.debug;

import java.io.IOException;

import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateErrorHandler implements ResponseErrorHandler
{
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException
    {
        return (httpResponse.getStatusCode().series() == Series.CLIENT_ERROR || 
                httpResponse.getStatusCode().series() == Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException
    {
        //TODO: handle these properly
        if (httpResponse.getStatusCode().series() == Series.SERVER_ERROR)
        {
            System.out.println("HTTP service encountered a server error: " + httpResponse.getStatusCode() + " " + httpResponse.getStatusText());
        }
        else if (httpResponse.getStatusCode().series() == Series.CLIENT_ERROR)
        {
            System.out.println("HTTP service encountered a client error: " + httpResponse.getStatusCode() + " " + httpResponse.getStatusText());
        }
    }

}