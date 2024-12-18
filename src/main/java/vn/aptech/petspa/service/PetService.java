package vn.aptech.petspa.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.dto.PetTypeDTO;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.PetHealth;
import vn.aptech.petspa.entity.PetPhoto;
import vn.aptech.petspa.entity.PetType;
import vn.aptech.petspa.repository.PetHealthRepository;
import vn.aptech.petspa.repository.PetPhotoRepository;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;

@Service
public class PetService {

    private final PetRepository petRepository;

    private final PetTypeRepository petTypeRepository;
    private final PetHealthRepository petHealthRepository;
    private final PetPhotoRepository petPhotoRepository;

    private final FileService fileService;

    @Autowired
    public PetService(
            PetRepository petRepository,
            FileService fileService,
            PetTypeRepository petTypeRepository,
            PetHealthRepository petHealthRepository,
            PetPhotoRepository petPhotoRepository) {
        this.petRepository = petRepository;
        this.fileService = fileService;
        this.petTypeRepository = petTypeRepository;
        this.petHealthRepository = petHealthRepository;
        this.petPhotoRepository = petPhotoRepository;
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

            PetHealth petHealth = new PetHealth();
            petHealth.setPet(pet);
            petHealth.setWeight(petDTO.getWeight());
            petHealth.setHeight(petDTO.getHeight());

            pet.setPetType(petTypeRepository.findById(petDTO.getPetTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet type not found")));

            PetPhoto petPhoto = new PetPhoto();
            petPhoto.setPet(pet);
            petPhoto.setUrl(fileUrl);
            petPhoto.setUploadedBy(userId);

            // Lưu Pet
            petRepository.save(pet);
            petHealthRepository.save(petHealth);
            petPhotoRepository.save(petPhoto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add pet: " + e.getMessage());
        }
    }

    public boolean checkPetNameExists(Long id, String name, Long long1) {
        return petRepository.existsByNameAndUserIdAndDeletedFalse(name, id);
    }

    // Phương thức phân trang pets
    public Page<Pet> retrieveAllPets(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    public List<PetType> retrievePetTypes() {
        return petTypeRepository.findAll();
    }

    public Page<Pet> retrievePetsByType(String type, Pageable pageable) {
        return petRepository.findByPetType_NameAndDeletedFalse(type, pageable);
    }

    public void addPetType(PetTypeDTO petTypeDTO) {
        PetType petType = new PetType();
        petType.setName(petTypeDTO.getName());
        petType.setDescription(petTypeDTO.getDescription());
        petTypeRepository.save(petType);
    }

    public void deletePetType(Long id) {
        PetType petType = petTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PetType not found"));
        petType.softDelete();
        petTypeRepository.save(petType);
    }

    public void updatePetType(PetTypeDTO petTypeDTO) {
        PetType petType = petTypeRepository.findById(petTypeDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("PetType not found"));
        petType.setName(petTypeDTO.getName());
        petType.setDescription(petTypeDTO.getDescription());
        petTypeRepository.save(petType);
    }
}
