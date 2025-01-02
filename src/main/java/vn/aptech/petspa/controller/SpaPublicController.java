package vn.aptech.petspa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.service.SpaService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;

@RestController
@RequestMapping("/api/public/spa")
public class SpaPublicController {
    // [
    // {
    // title: "Title od sevice category 1",
    // description: "Description of service category1",
    // items: [
    // {
    // image: `images`, // Image for the service
    // name: "Name of service", // Name of the service
    // description: "Description of service", // Description of the service
    // route: "/sevice-{cat-id}/{sevice-id}", // Route for the service
    // }
    // ],
    // },
    // {
    // title: "Title od sevice category 2",
    // description: "Description of service category2",
    // items: [
    // {
    // image: `images`, // Image for the service
    // name: "Name of service", // Name of the service
    // description: "Description of service", // Description of the service
    // route: "/sevice-{cat-id}/{sevice-id}", // Route for the service
    // },
    // {
    // image: `images`, // Image for the service
    // name: "Name of service", // Name of the service
    // description: "Description of service", // Description of the service
    // route: "/sevice-{cat-id}/{sevice-id}", // Route for the service
    // },

    // ],
    // },

    // ]

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SpaService spaService;

    @GetMapping("/all-services")
    public ResponseEntity<ApiResponse> getServices() {
        try {
            List<SpaCategoriesDTO> spaCategoriesDTOs = spaService.getAllServicesDTO();
            return ResponseEntity.ok(new ApiResponse(spaCategoriesDTOs));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }


    //count Spa categories
    @GetMapping("/count-categories")
    public ResponseEntity<ApiResponse> countCategories() {
        try {
            Long count = spaService.countCategories();
            return ResponseEntity.ok(new ApiResponse(count));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }
    
    //count Spa products
    @GetMapping("/count-products")
    public ResponseEntity<ApiResponse> countProducts() {
        try {
            Long count = spaService.countProducts();
            return ResponseEntity.ok(new ApiResponse(count));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }



}
