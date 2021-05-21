package com.vfts.trade.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * HttpClient class
 * for requesting netWorth of funds through third-party API
 * @author Axl
 */
@Service
public class HttpClient{
    public String client(String url) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.getForEntity(url, String.class);
        return response.getBody();
    }
}