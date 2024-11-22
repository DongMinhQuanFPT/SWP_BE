package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.*;

@Service
public class RoutingService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    GeoCodingService geoCodingService;


    private final String GRAPH_HOPPER_API_URL = "https://graphhopper.com/api/1/route";
    private final String API_KEY = "796003e4-061b-4a54-b815-3098412048a8";


    public String getFormattedRoute(double lat1, double lon1, double lat2, double lon2) {
        String url = GRAPH_HOPPER_API_URL + "?point=" + lat1 + "," + lon1 + "&point=" + lat2 + "," + lon2
                + "&type=json&points_encoded=false&key=" + API_KEY;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                assert responseBody != null;
                return formatRouteResponse(responseBody, lat1, lon1, lat2, lon2);
            } else {
                return "Error: Unable to fetch the route.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String getFormattedRouteThroughHue(double lat1, double lon1, double waypointLat, double waypointLon, double lat2, double lon2){
        String url = GRAPH_HOPPER_API_URL
                + "?point=" + lat1 + "," + lon1
                + "&point=" + waypointLat + "," + waypointLon
                + "&point=" + lat2 + "," + lon2
                + "&type=json&points_encoded=false&key=" + API_KEY;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                assert responseBody != null;
                return formatRouteResponse(responseBody, lat1, lon1, lat2, lon2);
            } else {
                return "Error: Unable to fetch the route.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public boolean inNorthSide(double lat1, double lon1, double lat2, double lon2){
        String url = GRAPH_HOPPER_API_URL + "?point=" + lat1 + "," + lon1 + "&point=" + lat2 + "," + lon2
                + "&type=json&key=" + API_KEY;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                assert responseBody != null;
                String responseRoute = formatRouteTotalDistanceResponse(responseBody);
                double distance = extractDistance(responseRoute);
                return !(distance > 500.0);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean inSouthSide(double lat1, double lon1, double lat2, double lon2){
        String url = GRAPH_HOPPER_API_URL + "?point=" + lat1 + "," + lon1 + "&point=" + lat2 + "," + lon2
                + "&type=json&key=" + API_KEY;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                assert responseBody != null;
                String responseRoute = formatRouteTotalDistanceResponse(responseBody);
                double distance = extractDistance(responseRoute);
                return !(distance > 500.0);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public String getDistanceFormattedRoute(double lat1, double lon1, double lat2, double lon2) {
        String url = GRAPH_HOPPER_API_URL + "?point=" + lat1 + "," + lon1 + "&point=" + lat2 + "," + lon2
                + "&type=json&points_encoded=false&key=" + API_KEY;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                assert responseBody != null;
                return formatRouteTotalDistanceResponse(responseBody);
            } else {
                return "Error: Unable to fetch the route.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public double extractDistance(String routeInfo) {
        String[] lines = routeInfo.split("\n");
        String totalDistance = null;
        for (String line : lines) {
            if (line.startsWith("Total Distance: ")) {
                totalDistance = line;
                break;
            }
        }
        if (totalDistance != null) {
            String[] parts = totalDistance.split(":");
            if (parts.length > 1) {
                String distanceValue = parts[1].trim();
                return Double.parseDouble(distanceValue.split(" ")[0]);
            }
        }
        throw new EntityNotFoundException("Cant find distance");
    }

    private String formatRouteTotalDistanceResponse(Map<String,Object> responseBody){
        List<Map<String, Object>> paths = (List<Map<String, Object>>) responseBody.get("paths");
        if (paths != null && !paths.isEmpty()) {
            Map<String, Object> path = paths.get(0);

            double distance = getDoubleValue(path.get("distance"));
            return "Total Distance: " + (distance / 1000) + " km";
        } else {
            return "No route data available.";
        }
    }

    private String formatRouteResponse(Map<String, Object> responseBody, double lat1, double lon1, double lat2, double lon2) {
        List<Map<String, Object>> paths = (List<Map<String, Object>>) responseBody.get("paths");

        if (paths != null && !paths.isEmpty()) {
            Map<String, Object> path = paths.get(0);  // Assuming only one path is needed

            double distance = getDoubleValue(path.get("distance"));
            double timeInMillis = getDoubleValue(path.get("time"));
            List<Map<String, Object>> instructions = (List<Map<String, Object>>) path.get("instructions");

            // Truy cập vào "points" như một Map và lấy danh sách tọa độ từ "coordinates"
            Map<String, Object> pointsMap = (Map<String, Object>) path.get("points");
            List<List<Double>> coordinates = (List<List<Double>>) pointsMap.get("coordinates");

            StringBuilder formattedResponse = new StringBuilder();
            formattedResponse.append("Total Distance: ").append(distance / 1000).append(" km\n");
            formattedResponse.append("Total Time: ").append(formatTime(timeInMillis)).append("\n");
            formattedResponse.append("Instructions:\n");

            // Danh sách để lưu trữ các tọa độ từ chỉ dẫn
            List<String> coordinatesList = new ArrayList<>();

            for (Map<String, Object> instruction : instructions) {
                String instructionText = (String) instruction.get("text");
                double instructionDistance = getDoubleValue(instruction.get("distance"));
                formattedResponse.append("- ").append(instructionText)
                        .append(" (").append(instructionDistance / 1000).append(" km)\n");

                // Kiểm tra và lấy tọa độ từ chỉ dẫn, nếu có
                if (instruction.containsKey("interval")) {
                    List<Integer> interval = (List<Integer>) instruction.get("interval");
                    if (interval.size() >= 2) {
                        // Giả sử tọa độ đầu tiên và cuối cùng của mỗi đoạn hướng dẫn
                        int startIndex = interval.get(0);
                        int endIndex = interval.get(1);

                        if (coordinates != null && startIndex < coordinates.size() && endIndex < coordinates.size()) {
                            List<Double> startPoint = coordinates.get(startIndex);
                            List<Double> endPoint = coordinates.get(endIndex);
                            coordinatesList.add("Start: [" + startPoint.get(1) + ", " + startPoint.get(0) + "], End: [" + endPoint.get(1) + ", " + endPoint.get(0) + "]");
                        }
                    }
                }
            }

            formattedResponse.append("\nStart Location: ").append(lat1).append(", ").append(lon1).append("\n");
            formattedResponse.append("Coordinates List:\n");
            for (String coord : coordinatesList) {
                formattedResponse.append(coord).append("\n");
            }
            formattedResponse.append("End Location: ").append(lat2).append(", ").append(lon2);

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
}
