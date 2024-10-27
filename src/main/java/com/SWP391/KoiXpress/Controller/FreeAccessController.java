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
@SecurityRequirement(name="api")
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

    @GetMapping("/allBlog")
    public ResponseEntity<PagedResponse<AllBlogResponse>> getAllBlogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        PagedResponse<AllBlogResponse> blogResponses = blogService.getAllBlog(page - 1, size);
        return ResponseEntity.ok(blogResponses);
    }

    @GetMapping("/calculateBoxAndSuggestFishSizes")
    public ResponseEntity<Map<String, Object>> calculateBoxAndSuggestFishSizes(
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


    @GetMapping("/route")
    public ResponseEntity<String> route(@RequestParam String startLocation, @RequestParam String endLocation) {
        try {
            double[] startCoords = geoCodingService.geocoding(startLocation);
            double[] endCoords = geoCodingService.geocoding(endLocation);

            String route = routingService.getFormattedRoute(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating route: " + e.getMessage());
        }
    }


    @GetMapping("/trackingOrder")
    public ResponseEntity<List<ProgressResponse>> trackingOrder(UUID trackingOrder){
        List<ProgressResponse> progresses = progressService.trackingOrder(trackingOrder);
        return ResponseEntity.ok(progresses);
    }

}
