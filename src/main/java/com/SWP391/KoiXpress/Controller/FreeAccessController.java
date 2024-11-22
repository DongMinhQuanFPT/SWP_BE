package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Model.response.Blog.AllBlogResponse;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.SWP391.KoiXpress.Service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/free-access")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class FreeAccessController {

    @Autowired
    BlogService blogService;

    @Autowired
    BoxDetailService boxDetailService;

    @Autowired
    RoutingService routingService;

    @Autowired
    GeoCodingService geoCodingService;

    @Autowired
    ProgressService progressService;


    //////////////////////Get-All-Blogs///////////////////////////
    @GetMapping("/allBlog")
    public ResponseEntity<PagedResponse<AllBlogResponse>> getAllBlogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        PagedResponse<AllBlogResponse> blogResponses = blogService.getAllBlog(page - 1, size);
        return ResponseEntity.ok(blogResponses);
    }
    //////////////////////////////////////////////////////////////


    //////////////////////Estimate-Price-Box-Preview///////////////////////////
    @GetMapping("/calculateBoxAndSuggestFishSizes")
    public ResponseEntity<Map<String, Object>> estimatePriceBoxPreview(
            @RequestParam List<Integer> quantities,
            @RequestParam List<Double> fishSizes) {

        if (quantities.size() != fishSizes.size()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Quantity and FishSize not match"));
        }

        Map<Double, Integer> fishSizeQuantityMap = new HashMap<>();
        for (int i = 0; i < fishSizes.size(); i++) {
            fishSizeQuantityMap.put(fishSizes.get(i), quantities.get(i));
        }

        Map<String, Object> result = boxDetailService.calculateBoxAndSuggestFishSizes(fishSizeQuantityMap);

        return ResponseEntity.ok(result);
    }
    ///////////////////////////////////////////////////////////////////////////


    //////////////////////Route///////////////////////////
    @GetMapping("/route")
    public ResponseEntity<String> route(@RequestParam String startLocation, @RequestParam String endLocation) {
        try {
            double[] startCoords = geoCodingService.geocoding(startLocation);
            double[] endCoords = geoCodingService.geocoding(endLocation);
            if (startCoords == null || endCoords == null || startCoords[1] < 2 || endCoords[1] < 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid start or end location.");
            }
            double[][] targetCoords = {
                    {21.0283334, 105.854041},
                    {10.7763897, 106.7011391}
            };
            boolean startInNorth = false, startInSouth = false;
            boolean endInNorth = false, endInSouth = false;

            if (routingService.inNorthSide(startCoords[0], startCoords[1], 21.0283334,105.854041)) {
                startInNorth = true;
            } else if (routingService.inSouthSide(startCoords[0], startCoords[1], 10.7763897, 106.7011391)) {
                startInSouth = true;
            }


            if (routingService.inNorthSide(endCoords[0], endCoords[1], 21.0283334, 105.854041)) {
                endInNorth = true;
            } else if (routingService.inSouthSide(endCoords[0], endCoords[1], 10.7763897, 106.7011391)) {
                endInSouth = true;
            }


            if ((startInNorth && endInSouth) || (startInSouth && endInNorth))  {
                // Thực hiện hành động chung cho cả hai trường hợp
                String route = routingService.getFormattedRouteThroughHue(startCoords[0], startCoords[1], 16.4639321, 107.5863388, endCoords[0], endCoords[1]);
                return ResponseEntity.ok(route);
            } else {
                String route = routingService.getFormattedRoute(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);
                return ResponseEntity.ok(route);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating route: " + e.getMessage());
        }

    }
    /////////////////////////////////////////////////////////


    //////////////////////Tracking-Order///////////////////////////
    @GetMapping("/trackingOrder")
    public ResponseEntity<List<ProgressResponse>> trackingOrder(UUID trackingOrder) {
        List<ProgressResponse> progresses = progressService.trackingOrder(trackingOrder);
        return ResponseEntity.ok(progresses);
    }
    ///////////////////////////////////////////////////////////////

}
