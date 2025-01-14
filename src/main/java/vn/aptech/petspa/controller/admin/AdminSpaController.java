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
            @RequestBody SpaProductDTO productDTO) {

        spaService.deleteSpaProduct(null, productDTO);

        return ResponseEntity.ok(new ApiResponse("Delete product successfully"));
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> listCategories() {
        return ResponseEntity.ok(new ApiResponse(spaService.retrieveCategoriesAdmin()));
    }

    // add category
    @PostMapping("/category")
    public ResponseEntity<ApiResponse> addCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaCategoriesDTO categoryDTO = objectMapper.readValue(categoryJson, SpaCategoriesDTO.class);

        spaService.addCategory(categoryDTO);

        return ResponseEntity.ok(new ApiResponse("Add category successfully"));
    }

    // update category
    @PostMapping("/category/update")
    public ResponseEntity<ApiResponse> updateCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        SpaCategoriesDTO categoryDTO = objectMapper.readValue(categoryJson, SpaCategoriesDTO.class);

        spaService.updateCategory(categoryDTO);

        return ResponseEntity.ok(new ApiResponse("Update category successfully"));
    }

    // delete category
    @PostMapping("/category/delete")
    public ResponseEntity<ApiResponse> deleteCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam("categoryId") Long categoryId) {

        spaService.deleteCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse("Delete category successfully"));
    }

}
