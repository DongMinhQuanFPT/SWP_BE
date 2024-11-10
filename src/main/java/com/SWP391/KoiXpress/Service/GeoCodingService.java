package com.SWP391.KoiXpress.Service;


import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoCodingService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private final String GRAPH_HOPPER_API_URL = "https://graphhopper.com/api/1/geocode";
    private final String API_KEY = "229b31d7-7c9e-4b27-a1c0-64f08f12f6e3";

    public double[] geocoding(String location) throws Exception{
        try{
            String url = String.format("%s?q=%s&key=%s", GRAPH_HOPPER_API_URL, location,API_KEY);
            String jsonResponse = restTemplate.getForObject(url,String.class);


            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode point = rootNode.path("hits").get(0).path("point");
            double lat = point.path("lat").asDouble();
            double lng = point.path("lng").asDouble();
            return new double[]{lat, lng};
        }catch(Exception e){
            throw new NotFoundException("Location cant find!");
        }
    }

}


