package vn.aptech.petspa.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.dto.SpaProductDTO;
import vn.aptech.petspa.service.SpaService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.PagedApiResponse;
import vn.aptech.petspa.util.ZDebug;

@RestController
@RequestMapping("/api/admin/spa-product")
public class AdminSpaController {

    @Autowired
    private SpaService spaService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listSpaProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {

        if (page < 0 || size <= 0) {
            return ApiResponse.badRequest("Invalid page or size values");
        }
        size = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, size);

        try {
            Page<SpaProductDTO> productDTOPage = spaService.getSpaProducts(name, categoryId, pageable);
            return ResponseEntity.ok(new PagedApiResponse(
                    "Successfully retrieved products",
                    productDTOPage.getContent(),
                    productDTOPage.getNumber(),
                    productDTOPage.getSize(),
                    productDTOPage.getTotalElements(),
                    productDTOPage.getTotalPages()));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @PostMapping(value = "/add", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> addSpaProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("productDTO") String productDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaProductDTO productDTO = objectMapper.readValue(productDTOJson, SpaProductDTO.class);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        spaService.addSpaProduct(null, productDTO, file);

        return ResponseEntity.ok(new ApiResponse("Add product successfully"));
    }

    @PostMapping(value = "/edit", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> editSpaProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("productDTO") String productDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaProductDTO productDTO = objectMapper.readValue(productDTOJson, SpaProductDTO.class);
        ZDebug.gI().ZigDebug("productDTO: " + productDTO);
        if (file != null && !file.isEmpty()) {
            spaService.editSpaProductWithImage(null, productDTO, file);
        } else {
            spaService.editSpaProductWithoutImage(null, productDTO);
        }

        return ResponseEntity.ok(new ApiResponse("Edit product successfully"));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteSpaProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("productDTO") String productDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaProductDTO productDTO = objectMapper.readValue(productDTOJson, SpaProductDTO.class);

        int del = spaService.deleteSpaProduct(null, productDTO);

        return ResponseEntity.ok(new ApiResponse(del == 1 ? "Hide product successfully" : "Show product successfully"));
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> listCategories() {
        return ResponseEntity.ok(new ApiResponse(spaService.retrieveCategoriesAdmin()));
    }

    // add category
    @PostMapping(value = "/category/add", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> addCategory(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaCategoriesDTO categoryDTO = objectMapper.readValue(categoryJson, SpaCategoriesDTO.class);

        spaService.addCategory(categoryDTO, file);

        return ResponseEntity.ok(new ApiResponse("Add category successfully"));
    }

    // update category
    @PostMapping(value = "/category/update", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> updateCategory(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaCategoriesDTO categoryDTO = objectMapper.readValue(categoryJson, SpaCategoriesDTO.class);

        spaService.updateCategory(categoryDTO, file);

        return ResponseEntity.ok(new ApiResponse("Update category successfully"));
    }

    // delete category
    @PostMapping("/category/delete")
    public ResponseEntity<ApiResponse> deleteCategory(
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SpaCategoriesDTO categoryDTO = objectMapper.readValue(categoryJson, SpaCategoriesDTO.class);

        int del = spaService.deleteCategory(categoryDTO.getId());
        return ResponseEntity
                .ok(new ApiResponse(del == 1 ? "Delete category successfully" : "Show category successfully"));
    }

}
