package vn.aptech.petspa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import vn.aptech.petspa.dto.OrderDTO;
import vn.aptech.petspa.dto.OrderRequestDTO;
import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.OrderService;
import vn.aptech.petspa.service.PetService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.GoodsType;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.PagedApiResponse;
import vn.aptech.petspa.util.ZDebug;

@RestController
@RequestMapping("/api/user-order")
public class UserOrderController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listUserPet(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String goodsType) {
        Long userId = jwtUtil.extractUserId(token);
        if (userId == 0) {
            return ApiResponse.unauthorized("Invalid token");
        }

        if (page < 0 || size <= 0) {
            return ApiResponse.badRequest("Invalid page or size values");
        }
        size = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, size);

        GoodsType type = null;
        if (goodsType != null) {
            try {
                type = GoodsType.valueOf(goodsType.toUpperCase()); // Chuyển từ String sang Enum
                ZDebug.gI().ZigDebug("goodsType: " + type.toString());
            } catch (IllegalArgumentException e) {
                return ApiResponse.badRequest("Invalid goods type value");
            }
        }

        try {
            Page<OrderDTO> orderDTOPage = orderService.getUserOrder(userId, search, type, date, pageable);
            return ResponseEntity.ok(new PagedApiResponse(
                    "Successfully retrieved pets",
                    orderDTOPage.getContent(),
                    orderDTOPage.getNumber(),
                    orderDTOPage.getSize(),
                    orderDTOPage.getTotalElements(),
                    orderDTOPage.getTotalPages()));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @PostMapping(value = "/createOrder", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> addUserPet(
            @RequestHeader("Authorization") String token,
            @RequestParam("orderRequestDTO") String orderJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        OrderRequestDTO parsedorderDTO = objectMapper.readValue(orderJson, OrderRequestDTO.class);
        ZDebug.gI().ZigDebug(parsedorderDTO.toString());

        Order order = orderService.createOrder(parsedorderDTO);
        return ResponseEntity.ok(new ApiResponse("Create order successfully", order));
    }

    // order detail
    @GetMapping("/detail/{orderId}")
    public ResponseEntity<ApiResponse> orderDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        try {
            Long userId = jwtUtil.extractUserId(token);
            if (userId == 0) {
                return ApiResponse.unauthorized("Invalid token");
            }
            OrderDTO orderDTO = orderService.getOrderDetail(orderId);
            if (orderDTO == null) {
                return ApiResponse.notFound("Order not found");
            }
            if (orderDTO.getUserId() != userId) {
                return ApiResponse.unauthorized("You are not authorized to view this order");
            }

            return ResponseEntity.ok(new ApiResponse("Successfully retrieved order detail", orderDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    // cancel order
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse> cancelOrder(
            @PathVariable Long orderId) {

        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Cancel order successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }
}
