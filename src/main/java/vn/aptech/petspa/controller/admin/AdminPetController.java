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
import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.dto.PetTypeDTO;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.PetService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.PagedApiResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/admin/pet") // Pet của user
public class AdminPetController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetService petService;

    @GetMapping("/pets/all")
    public ResponseEntity<ApiResponse> listAllPets(
            @RequestParam(defaultValue = "0") int page, // Trang mặc định là 0
            @RequestParam(defaultValue = "10") int size // Kích thước mặc định là 10
    ) {
        if (page < 0 || size <= 0) {
            return ApiResponse.badRequest("Invalid page or size values");
        }
        size = Math.min(size, 100); // Giới hạn kích thước trang tối đa là 100
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable object
        Page<PetDTO> petDTOPage = petService.retrieveAllPets(pageable); // Gọi service với pageable
        // Page<PetDTO> petDTOPage = petPage.map(pet -> new PetDTO(pet)); // Chuyển đổi
        // từ Pet sang PetDTO
        return ResponseEntity.ok(new PagedApiResponse(
                "Successfully retrieved pets",
                petDTOPage.getContent(), // Danh sách pets
                petDTOPage.getNumber(), // Trang hiện tại
                petDTOPage.getSize(), // Kích thước mỗi trang
                petDTOPage.getTotalElements(), // Tổng số bản ghi
                petDTOPage.getTotalPages() // Tổng số trang
        ));
    }

    // get pet by type
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse> listPetsByType(
            @RequestParam(defaultValue = "0") int page, // Trang mặc định là 0
            @RequestParam(defaultValue = "10") int size, // Kích thước mặc định là 10
            @RequestParam String type // Loại pet
    ) {
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable object
        Page<Pet> petPage = petService.retrievePetsByType(type, pageable); // Gọi service với pageable

        return ResponseEntity.ok(new PagedApiResponse(
                "Successfully retrieved pets",
                petPage.getContent(), // Danh sách pets
                petPage.getNumber(), // Trang hiện tại
                petPage.getSize(), // Kích thước mỗi trang
                petPage.getTotalElements(), // Tổng số bản ghi
                petPage.getTotalPages() // Tổng số trang
        ));
    }

    // get pet type
    @GetMapping("/pet-types")
    public ResponseEntity<ApiResponse> listPetTypes() {
        return ResponseEntity.ok(new ApiResponse(petService.retrievePetTypes()));
    }

    // add pet type
    @PostMapping("/pet-type")
    public ResponseEntity<ApiResponse> addPetType(
            @RequestHeader("Authorization") String token,
            @RequestParam("petDTO") String petDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PetTypeDTO petTypeDTO = objectMapper.readValue(petDTOJson, PetTypeDTO.class);

        // Check for duplicate pet type name
        if (petService.isPetTypeNameExists(petTypeDTO.getName())) {
            return ApiResponse.badRequest("Pet type name already exists");
        }

        petService.addPetType(petTypeDTO);
        return ResponseEntity.ok(new ApiResponse("Successfully added pet type"));
    }

    // update pet type
    @PostMapping("/pet-type/update")
    public ResponseEntity<ApiResponse> updatePetType(
            @RequestHeader("Authorization") String token,
            @RequestParam("petTypeDTO") String petTypeJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PetTypeDTO petTypeDTO = objectMapper.readValue(petTypeJson, PetTypeDTO.class);

        // Check for duplicate pet type name
        if (petService.isPetTypeNameExists(petTypeDTO.getName())) {
            return ApiResponse.badRequest("Pet type name already exists");
        }

        petService.updatePetType(petTypeDTO);
        return ResponseEntity.ok(new ApiResponse("Successfully updated pet type"));
    }

    // delete pet type
    @PostMapping("/pet-type/delete")
    public ResponseEntity<ApiResponse> deletePetType(
            @RequestHeader("Authorization") String token,
            @RequestParam("petTypeDTO") String petTypeJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PetTypeDTO petTypeDTO = objectMapper.readValue(petTypeJson, PetTypeDTO.class);

        petService.deletePetType(petTypeDTO.getId());
        return ResponseEntity.ok(new ApiResponse("Successfully deleted pet type"));
    }
}
