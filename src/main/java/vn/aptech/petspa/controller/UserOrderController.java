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
import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.OrderService;
import vn.aptech.petspa.service.PetService;
import vn.aptech.petspa.util.ApiResponse;
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
            @RequestParam(defaultValue = "0") int page, // Trang mặc định là 0
            @RequestParam(defaultValue = "10") int size, // Kích thước mặc định là 10
            @RequestParam(required = false) String date, // Tìm kiếm theo tên (không bắt buộc)
            @RequestParam(required = false) String search, // Tìm kiếm theo tên (không bắt buộc)
            @RequestParam(required = false) String goodsType // Lọc theo loại pet (không bắt buộc)
    ) {
        Long userId = jwtUtil.extractUserId(token);
        if (userId == 0) {
            return ApiResponse.unauthorized("Invalid token");
        }

        if (page < 0 || size <= 0) {
            return ApiResponse.badRequest("Invalid page or size values");
        }
        size = Math.min(size, 100); // Giới hạn kích thước trang tối đa là 100
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable object

        try {
            Page<OrderDTO> orderDTOPage = orderService.getUserOrder(userId, search, goodsType, date, pageable);
            return ResponseEntity.ok(new PagedApiResponse(
                    "Successfully retrieved pets",
                    orderDTOPage.getContent(), // Danh sách pets
                    orderDTOPage.getNumber(), // Trang hiện tại
                    orderDTOPage.getSize(), // Kích thước mỗi trang
                    orderDTOPage.getTotalElements(), // Tổng số bản ghi
                    orderDTOPage.getTotalPages() // Tổng số trang
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }
}
