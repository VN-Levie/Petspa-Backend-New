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

import vn.aptech.petspa.dto.ShopCategoryDTO;
import vn.aptech.petspa.dto.ShopProductDTO;
import vn.aptech.petspa.service.ShopProductService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.PagedApiResponse;

@RestController
@RequestMapping("/api/admin/shop-product")
public class AdminShopProductController {

    @Autowired
    private ShopProductService shopProductService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listShopProducts(
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
            Page<ShopProductDTO> productDTOPage = shopProductService.getShopProducts(name, categoryId, pageable);
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
    public ResponseEntity<ApiResponse> addShopProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("productDTO") String productDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ShopProductDTO productDTO = objectMapper.readValue(productDTOJson, ShopProductDTO.class);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        shopProductService.addShopProduct(null, productDTO, file);

        return ResponseEntity.ok(new ApiResponse("Add product successfully"));
    }

    @PostMapping(value = "/edit", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> editShopProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("productDTO") String productDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ShopProductDTO productDTO = objectMapper.readValue(productDTOJson, ShopProductDTO.class);

        if (file != null && !file.isEmpty()) {
            shopProductService.editShopProductWithImage(null, productDTO, file);
        } else {
            shopProductService.editShopProductWithoutImage(null, productDTO);
        }

        return ResponseEntity.ok(new ApiResponse("Edit product successfully"));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteShopProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody ShopProductDTO productDTO) {

        shopProductService.deleteShopProduct(null, productDTO);

        return ResponseEntity.ok(new ApiResponse("Delete product successfully"));
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> listCategories() {
        return ResponseEntity.ok(new ApiResponse(shopProductService.retrieveCategoriesAdmin()));
    }

    // add category
    @PostMapping("/category")
    public ResponseEntity<ApiResponse> addCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ShopCategoryDTO categoryDTO = objectMapper.readValue(categoryJson, ShopCategoryDTO.class);

        shopProductService.addCategory(categoryDTO);

        return ResponseEntity.ok(new ApiResponse("Add category successfully"));
    }

    // update category
    @PostMapping("/category/update")
    public ResponseEntity<ApiResponse> updateCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam("categoryDTO") String categoryJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ShopCategoryDTO categoryDTO = objectMapper.readValue(categoryJson, ShopCategoryDTO.class);

        shopProductService.updateCategory(categoryDTO);

        return ResponseEntity.ok(new ApiResponse("Update category successfully"));
    }

    // delete category
    @PostMapping("/category/delete")
    public ResponseEntity<ApiResponse> deleteCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam("categoryId") Long categoryId) {

        shopProductService.deleteCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse("Delete category successfully"));
    }

}
