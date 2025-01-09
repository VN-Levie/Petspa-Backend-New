package vn.aptech.petspa.controller;

import java.util.List;

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

import vn.aptech.petspa.dto.ShopProductDTO;
import vn.aptech.petspa.entity.ShopProduct;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.ShopCategoryRepository;
import vn.aptech.petspa.repository.ShopProductRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.ShopProductService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.PagedApiResponse;

@RestController
@RequestMapping("/api/shop-product")
public class ShopProductController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopProductService shopProductService;

    @Autowired
    private ShopCategoryRepository shopCategoryRepository;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listShopProducts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {

        Long userId = jwtUtil.extractUserId(token);
        if (userId == 0) {
            return ApiResponse.unauthorized("Invalid token");
        }

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

        String email = jwtUtil.extractEmail(token);
        if (!shopCategoryRepository.existsById(productDTO.getCategoryId())) {
            throw new IllegalArgumentException("Category does not exist");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        shopProductService.addShopProduct(user, productDTO, file);

        return ResponseEntity.ok(new ApiResponse("Add product successfully"));
    }

    @PostMapping(value = "/edit", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> editShopProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("productDTO") String productDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ShopProductDTO productDTO = objectMapper.readValue(productDTOJson, ShopProductDTO.class);

        String email = jwtUtil.extractEmail(token);

        if (!shopCategoryRepository.existsById(productDTO.getCategoryId())) {
            throw new IllegalArgumentException("Category does not exist");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (file != null && !file.isEmpty()) {
            shopProductService.editShopProductWithImage(user, productDTO, file);
        } else {
            shopProductService.editShopProductWithoutImage(user, productDTO);
        }

        return ResponseEntity.ok(new ApiResponse("Edit product successfully"));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteShopProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody ShopProductDTO productDTO) {

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        shopProductService.deleteShopProduct(user, productDTO);

        return ResponseEntity.ok(new ApiResponse("Delete product successfully"));
    }
}
