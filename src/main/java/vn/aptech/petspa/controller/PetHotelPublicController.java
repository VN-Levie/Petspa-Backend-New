package vn.aptech.petspa.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.aptech.petspa.dto.*;
import vn.aptech.petspa.entity.*;
import vn.aptech.petspa.repository.*;
import vn.aptech.petspa.service.*;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.PagedApiResponse;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/public/hotel")
public class PetHotelPublicController {

    @Autowired
    private HotelService hotelService;

    // get all room type
    @GetMapping(value = "/roomType/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getAllRoomType() {
        List<PetHotelRoomTypeDTO> roomTypes = hotelService.getAllRoomType();
        return ResponseEntity.ok(new ApiResponse("Get all room type successfully", roomTypes));
    }

    // get all room
    @GetMapping(value = "/room/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getAllRoom() {
        List<PetHotelRoomDTO> rooms = hotelService.getAllRoom();
        return ResponseEntity.ok(new ApiResponse("Get all room successfully", rooms));
    }

    //get room type by id
    @GetMapping(value = "/roomType/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getRoomType(
            @RequestParam("id") Long id) {
        PetHotelRoomTypeDTO roomType = hotelService.getRoomType(id);
        return ResponseEntity.ok(new ApiResponse("Get room type successfully", roomType));
    }

    //get all room by room type
    @GetMapping(value = "/room/getByRoomType", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getRoomByRoomType(
            @RequestParam("roomTypeId") Long roomTypeId) {
        List<PetHotelRoomDTO> rooms = hotelService.getRoomByRoomType(roomTypeId);
        return ResponseEntity.ok(new ApiResponse("Get room by room type successfully", rooms));
    }

}
