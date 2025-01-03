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
import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.service.PetService;
import vn.aptech.petspa.util.ApiResponse;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.ZDebug;

@RestController
@RequestMapping("/api/user-pet") // Pet của user
public class UserPetController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetService petService;

    @Autowired
    private PetTypeRepository petTypeRepository;

    // lấy danh sách pet của user
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> listUserPet(@RequestHeader("Authorization") String token) {

        Long userId = jwtUtil.extractUserId(token);
        if (userId == 0) {
            return ApiResponse.unauthorized("Invalid token");
        }

        try {
            List<PetDTO> petDTOs = petService.getUserPets(userId);
            return ResponseEntity.ok(new ApiResponse(petDTOs));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }

    }

    // thêm pet cho user
    @PostMapping(value = "/add", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> addUserPet(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("petDTO") String petDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PetDTO petDTO = objectMapper.readValue(petDTOJson, PetDTO.class);

        String email = jwtUtil.extractEmail(token);
        ZDebug.gI().ZigDebug("email: " + email + "token: " + token);
        if (!petTypeRepository.existsById(petDTO.getPetTypeId())) {
            throw new IllegalArgumentException("Pet type does not exist");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("add pet.User not found"));

        if (petService.checkPetNameExists(user.getId(), petDTO.getName(), petDTO.getPetTypeId())) {
            throw new IllegalArgumentException("Pet name already exists");
        }

        petService.addPet(user, petDTO, file);

        return ResponseEntity.ok(new ApiResponse("Add pet successfully"));
    }

    // edit
    @PostMapping(value = "/edit", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> editUserPet(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("petDTO") String petDTOJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PetDTO petDTO = objectMapper.readValue(petDTOJson, PetDTO.class);

        String email = jwtUtil.extractEmail(token);

        if (!petTypeRepository.existsById(petDTO.getPetTypeId())) {
            throw new IllegalArgumentException("Pet type does not exist");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

       

        // Gọi service khác nếu không có file
        if (file != null && !file.isEmpty()) {
            petService.editPetWithAvatar(user, petDTO, file);
        } else {
            petService.editPetWithoutAvatar(user, petDTO);
        }

        return ResponseEntity.ok(new ApiResponse("Edit pet successfully"));
    }

    // delete
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteUserPet(@RequestHeader("Authorization") String token,
            @RequestBody PetDTO petDTO) {

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        petService.deletePet(user, petDTO);

        return ResponseEntity.ok(new ApiResponse("Delete pet successfully"));
    }

    // count pet
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countUserPet(@RequestHeader("Authorization") String token) {

        Long userId = jwtUtil.extractUserId(token);
        if (userId == 0) {
            return ApiResponse.unauthorized("Invalid token");
        }

        try {
            Long count = petService.countUserPet(userId);
            return ResponseEntity.ok(new ApiResponse(count));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }

    }

    // get pet type
    @GetMapping("/pet-type")
    public ResponseEntity<ApiResponse> getPetTypes() {
        try {
            return ResponseEntity.ok(new ApiResponse(petService.retrievePetTypes()));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.badRequest(e.getMessage());
        }
    }
}
