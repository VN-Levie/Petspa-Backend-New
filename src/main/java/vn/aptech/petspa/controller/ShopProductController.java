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

    // get product by id
    @GetMapping("/product")
    public ResponseEntity<ApiResponse> getProductById(@RequestParam Long productId) {

        try {
            ShopProductDTO productDTO = shopProductService.getShopProductById(productId);
            return ResponseEntity.ok(new ApiResponse(productDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

}
