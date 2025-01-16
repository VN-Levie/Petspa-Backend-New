package vn.aptech.petspa.controller.admin;

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
@RequestMapping("/api/admin/hotel")
public class AdminHotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping(value = "/room/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> addRoom(
            @RequestParam("petHotelRoomDTO") String hotelRoomJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PetHotelRoomDTO categoryDTO = objectMapper.readValue(hotelRoomJson, PetHotelRoomDTO.class);

        hotelService.addRoom(categoryDTO);
        return ResponseEntity.ok(new ApiResponse("Add room successfully"));
    }

    // edit room
    @PostMapping(value = "/room/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> editRoom(
            @RequestParam("petHotelRoomDTO") String hotelRoomJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PetHotelRoomDTO categoryDTO = objectMapper.readValue(hotelRoomJson, PetHotelRoomDTO.class);

        hotelService.editRoom(categoryDTO);
        return ResponseEntity.ok(new ApiResponse("Edit room successfully"));
    }

    // delete room
    @PostMapping(value = "/room/delete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> deleteRoom(
            @RequestParam("id") Long id) {
        int del = hotelService.deleteRoom(id);
        return ResponseEntity.ok(new ApiResponse(del == 0 ? "Hide room successfully" : "Show room successfully"));
    }

    // get room by id
    @GetMapping(value = "/room/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getRoom(
            @RequestParam("id") Long id) {
        PetHotelRoomDTO room = hotelService.getRoom(id);
        return ResponseEntity.ok(new ApiResponse("Get root successfully", room));
    }

    //get room type by id
    @GetMapping(value = "/roomType/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getRoomType(
            @RequestParam("id") Long id) {
        PetHotelRoomTypeDTO roomType = hotelService.getRoomType(id);
        return ResponseEntity.ok(new ApiResponse("Get room type successfully", roomType));
    }

    // get all room
    @GetMapping(value = "/room/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getAllRoom() {
        List<PetHotelRoomDTO> rooms = hotelService.getAllRoom();
        return ResponseEntity.ok(new ApiResponse("Get all room successfully", rooms));
    }

    // get all room type
    @GetMapping(value = "/roomType/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> getAllRoomType() {
        List<PetHotelRoomTypeDTO> roomTypes = hotelService.getAllRoomType();
        return ResponseEntity.ok(new ApiResponse("Get all room type successfully", roomTypes));
    }

    // add room type
    @PostMapping(value = "/roomType/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> addRoomType(
            @RequestParam("petHotelRoomTypeDTO") String roomTypeJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PetHotelRoomTypeDTO roomTypeDTO = objectMapper.readValue(roomTypeJson, PetHotelRoomTypeDTO.class);

        hotelService.addRoomType(roomTypeDTO);
        return ResponseEntity.ok(new ApiResponse("Add room type successfully"));
    }

    // edit room type
    @PostMapping(value = "/roomType/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> editRoomType(
            @RequestParam("petHotelRoomTypeDTO") String roomTypeJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PetHotelRoomTypeDTO roomTypeDTO = objectMapper.readValue(roomTypeJson, PetHotelRoomTypeDTO.class);

        hotelService.editRoomType(roomTypeDTO);
        return ResponseEntity.ok(new ApiResponse("Edit room type successfully"));
    }

    // delete room type
    @PostMapping(value = "/roomType/delete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> deleteRoomType(
            @RequestParam("petHotelRoomTypeDTO") String categoryJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PetHotelRoomTypeDTO categoryDTO = objectMapper.readValue(categoryJson, PetHotelRoomTypeDTO.class);
        int del = hotelService.deleteRoomType(categoryDTO.getId());
        return ResponseEntity
                .ok(new ApiResponse(del == 0 ? "Hide room type successfully" : "Show room type successfully"));
    }

}
