package vn.aptech.petspa.controller;

import java.util.List;
import java.util.Optional;

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
import vn.aptech.petspa.dto.SpaProductDTO;
import vn.aptech.petspa.service.CustomUserDetailsService;
import vn.aptech.petspa.service.SpaService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.EnumSet;

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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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

    @RequestMapping(value = "/all-services", method = RequestMethod.OPTIONS)
    public ResponseEntity<ApiResponse> handleOptions() {

        return getServices();
    }

    // Get services by category
    @GetMapping("/services-by-category")
    public ResponseEntity<ApiResponse> getServicesByCategory(@RequestParam Long categoryId) {
        try {
            Optional<List<SpaProductDTO>> spaCategoriesDTOs = spaService.getServicesByCategory(categoryId);
            if (spaCategoriesDTOs.isEmpty() || spaCategoriesDTOs.get().isEmpty()) {
                return ApiResponse.notFound("Category not found");
            }
            return ResponseEntity.ok(new ApiResponse(spaCategoriesDTOs));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    // Get services by id
    @GetMapping("/services-by-id")
    public ResponseEntity<ApiResponse> getServicesById(@RequestParam Long id) {
        try {
            Optional<SpaProductDTO> spaCategoriesDTOs = spaService.getServicesById(id);
            if (spaCategoriesDTOs.isEmpty() || spaCategoriesDTOs.get().isDeleted()) {
                return ApiResponse.notFound("Service not found");
            }
            return ResponseEntity.ok(new ApiResponse(spaCategoriesDTOs));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    // count Spa categories
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

    // count Spa products
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

    // count user
    @GetMapping("/count-users")
    public ResponseEntity<ApiResponse> countUsers() {
        try {
            Long count = customUserDetailsService.countUsers();
            return ResponseEntity.ok(new ApiResponse(count));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

}
