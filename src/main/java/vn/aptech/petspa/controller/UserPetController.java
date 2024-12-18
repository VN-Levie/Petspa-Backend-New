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

@RestController
@RequestMapping("/user-pet") // Pet của user
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
        try {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            List<Pet> pets = petService.fetchUserPets(user.getId());
            return ResponseEntity.ok(new ApiResponse(pets));
        } catch (JwtException e) {
            return jwtUtil.responseUnauthorized();
        } catch (Exception e) {
            return jwtUtil.responseInternalServerError(e);
        }
    }

    // thêm pet cho user
    @PostMapping(value = "/add", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse> addUserPet(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("petDTO") String petDTOJson) throws JsonProcessingException {

        // Parse JSON string thành PetDTO
        ObjectMapper objectMapper = new ObjectMapper();
        PetDTO petDTO = objectMapper.readValue(petDTOJson, PetDTO.class);

        // Lấy email từ JWT
        String email = jwtUtil.extractEmail(token);

        // Kiểm tra pet type
        if (!petTypeRepository.existsById(petDTO.getPetTypeId())) {
            throw new IllegalArgumentException("Pet type does not exist");
        }

        // Kiểm tra file upload
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        // Lấy thông tin user từ email
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Kiểm tra tên pet
        if (petService.checkPetNameExists(user.getId(), petDTO.getName(), petDTO.getPetTypeId())) {
            throw new IllegalArgumentException("Pet name already exists");
        }

        // Gọi service để thêm pet
        petService.addPet(user.getId(), petDTO, file);

        // Trả về kết quả thành công
        return ResponseEntity.ok(new ApiResponse("Add pet successfully"));
    }

}
