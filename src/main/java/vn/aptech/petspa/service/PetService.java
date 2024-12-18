package vn.aptech.petspa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.repository.PetRepository;

import java.io.IOException;
import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    private final FileService fileService;

    @Autowired
    public PetService(PetRepository petRepository, FileService fileService) {
        this.petRepository = petRepository;
        this.fileService = fileService;
    }

    public List<Pet> fetchUserPets(Long userId) {
        // Trả về danh sách Pet chưa bị xóa
        return petRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<Pet> retrievePetsForAdmin(Long userId) {
        // Trả về tất cả Pet của user (bao gồm deleted)
        return petRepository.findByUserId(userId);
    }

    public void addPet(Long userId, PetDTO petDTO, MultipartFile file) {
        try {
            // Kiểm tra kích thước file
            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }

            // Kiểm tra định dạng file
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            // Lưu file và lấy đường dẫn
            String uploadDir = "uploads/pets"; // Thư mục lưu trữ
            String fileUrl = fileService.uploadFile(file, uploadDir);

            // Tạo Pet entity
            Pet pet = new Pet();
            pet.setName(petDTO.getName());
            pet.setDescription(petDTO.getDescription());
            pet.setUserId(userId);
            pet.setAvatarUrl(fileUrl);

            petRepository.save(pet);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add pet: " + e.getMessage());
        }
    }

    public boolean checkPetNameExists(Long id, String name, Long long1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkPetNameExists'");
    }
}
