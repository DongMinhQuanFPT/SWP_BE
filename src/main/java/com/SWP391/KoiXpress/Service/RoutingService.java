package com.SWP391.KoiXpress.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class RoutingService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    GeoCodingService geoCodingService;


    private final String GRAPH_HOPPER_API_URL = "https://graphhopper.com/api/1/route";
    private final String API_KEY = "796003e4-061b-4a54-b815-3098412048a8";

    public String getFormattedRoute(double lat1, double lon1, double lat2, double lon2) {
        String url = GRAPH_HOPPER_API_URL + "?point=" + lat1 + "," + lon1 + "&point=" + lat2 + "," + lon2 + "&type=json&key=" + API_KEY;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                return formatRouteResponse(responseBody);
            } else {
                return "Error: Unable to fetch the route.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String formatRouteResponse(Map<String, Object> responseBody) {
        List<Map<String, Object>> paths = (List<Map<String, Object>>) responseBody.get("paths");

        if (paths != null && !paths.isEmpty()) {
            Map<String, Object> path = paths.get(0);  // Assuming only one path is needed

            double distance = getDoubleValue(path.get("distance"));
            double timeInMillis = getDoubleValue(path.get("time"));
            List<Map<String, Object>> instructions = (List<Map<String, Object>>) path.get("instructions");

            StringBuilder formattedResponse = new StringBuilder();
            formattedResponse.append("Total Distance: ").append(distance / 1000).append(" km\n");
            formattedResponse.append("Total Time: ").append(formatTime(timeInMillis)).append("\n\n");
            formattedResponse.append("Instructions:\n");

            for (Map<String, Object> instruction : instructions) {
                String instructionText = (String) instruction.get("text");
                double instructionDistance = getDoubleValue(instruction.get("distance"));
                formattedResponse.append("- ").append(instructionText)
                        .append(" (").append(instructionDistance / 1000).append(" km)\n");
            }

            return formattedResponse.toString();
        } else {
            return "No route data available.";
        }
    }

    private double getDoubleValue(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue(); // Convert Integer to Double
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            throw new IllegalArgumentException("Unexpected value type: " + value.getClass().getName());
        }
    }


    private String formatTime(double timeInMillis) {
        int hours = (int) (timeInMillis / 3600000);
        int minutes = (int) ((timeInMillis % 3600000) / 60000);
        return hours + " hrs " + minutes + " mins";
    }

    public String getRouteSegment(String startLocation, String endLocation) throws Exception {
        // Sử dụng GeoCodingService để lấy tọa độ cho startLocation và endLocation
        double[] startCoords = geoCodingService.geocoding(startLocation);
        double[] endCoords = geoCodingService.geocoding(endLocation);

        // Kiểm tra kết quả geocoding
        System.out.println("Start Coords: " + Arrays.toString(startCoords));
        System.out.println("End Coords: " + Arrays.toString(endCoords));

        String url = UriComponentsBuilder.fromHttpUrl(GRAPH_HOPPER_API_URL)
                .queryParam("point", startCoords[0] + "," + startCoords[1])
                .queryParam("point", endCoords[0] + "," + endCoords[1])
                .queryParam("vehicle", "truck")
                .queryParam("key", API_KEY)
                .toUriString();

        System.out.println("GraphHopper API URL: " + url); // In ra URL để kiểm tra

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // In ra toàn bộ phản hồi JSON từ API để kiểm tra
            System.out.println("API Response: " + response.getBody());

            return response.getBody(); // Trả về phản hồi JSON từ API
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching route segment: " + e.getMessage();
        }
    }

    public double[] geocoding(String location) {
        // Simulate geocoding here, or ideally, call a real geocoding service.
        // For example, returning fixed coordinates for simplicity here.
        return new double[]{ /* latitude */, /* longitude */ };
    }
}
