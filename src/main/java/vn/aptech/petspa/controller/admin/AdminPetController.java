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
@RequestMapping("/admin/pet") // Pet của user
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
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable object
        Page<Pet> petPage = petService.retrieveAllPets(pageable); // Gọi service với pageable

        return ResponseEntity.ok(new PagedApiResponse(
                "Successfully retrieved pets",
                petPage.getContent(), // Danh sách pets
                petPage.getNumber(), // Trang hiện tại
                petPage.getSize(), // Kích thước mỗi trang
                petPage.getTotalElements(), // Tổng số bản ghi
                petPage.getTotalPages() // Tổng số trang
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
    public ResponseEntity<ApiResponse> addPetType(@RequestBody PetTypeDTO petTypeDTO) {
        petService.addPetType(petTypeDTO);
        return ResponseEntity.ok(new ApiResponse("Successfully added pet type"));
    }

    // delete pet type
    @PostMapping("/pet-type/delete")
    public ResponseEntity<ApiResponse> deletePetType(@RequestParam Long id) {
        petService.deletePetType(id);
        return ResponseEntity.ok(new ApiResponse("Successfully deleted pet type"));
    }

    // update pet type
    @PostMapping("/pet-type/update")
    public ResponseEntity<ApiResponse> updatePetType(@RequestBody PetTypeDTO petTypeDTO) {
        petService.updatePetType(petTypeDTO);
        return ResponseEntity.ok(new ApiResponse("Successfully updated pet type"));
    }
}
