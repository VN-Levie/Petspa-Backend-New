package vn.aptech.petspa.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.repository.PetHealthRepository;
import vn.aptech.petspa.repository.PetPhotoRepository;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetTypeRepository petTypeRepository;
    @Autowired
    private PetHealthRepository petHealthRepository;
    @Autowired
    private PetPhotoRepository petPhotoRepository;

    @Autowired
    private FileService fileService;

    public List<Pet> fetchUserPets(Long userId) {
        // Trả về danh sách Pet chưa bị xóa
        return petRepository.findByUserIdAndDeletedFalse(userId);
    }

    // @Transactional(readOnly = true)
    public List<PetDTO> getUserPets(Long userId) {     
        return petRepository.findPetsWithHealths(userId);
    }

    public List<Pet> retrievePetsForAdmin(Long userId) {
        // Trả về tất cả Pet của user (bao gồm deleted)
        return petRepository.findByUserId(userId);
    }

    @Transactional
    public void addPet(User user, PetDTO petDTO, MultipartFile file) {
        try {
            // Kiểm tra kích thước và định dạng file
            if (!fileService.isImageSize(file.getSize())) {
                throw new IllegalArgumentException("File size exceeds the allowed limit.");
            }
            if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid image format.");
            }

            // Lưu file và lấy URL
            String uploadDir = "uploads/pets";
            String fileUrl = fileService.uploadFile(file, uploadDir);

            // Tạo Pet entity
            Pet pet = new Pet();
            pet.setName(petDTO.getName());
            pet.setDescription(petDTO.getDescription());
            pet.setUser(user);
            pet.setAvatarUrl(fileUrl);
            pet.setPetType(petTypeRepository.findById(petDTO.getPetTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet type not found")));

            // Lưu Pet vào DB
            petRepository.save(pet);

            // Tạo và lưu PetHealth
            PetHealth petHealth = new PetHealth();
            petHealth.setPet(pet);
            petHealth.setWeight(petDTO.getWeight());
            petHealth.setHeight(petDTO.getHeight());
            petHealthRepository.save(petHealth);

            // Tạo và lưu PetPhoto
            PetPhoto petPhoto = new PetPhoto();
            petPhoto.setPet(pet);
            petPhoto.setUrl(fileUrl);
            petPhoto.setUploadedBy(user);
            petPhotoRepository.save(petPhoto);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add pet: " + e.getMessage());
        }
    }

    // editPet | petService.editPet(user, petDTO, file);
    @Transactional
    public void editPet(User user, PetDTO petDTO, MultipartFile file) {
        try {
            // Kiểm tra kích thước và định dạng file
            if (file != null && !file.isEmpty()) {
                if (!fileService.isImageSize(file.getSize())) {
                    throw new IllegalArgumentException("File size exceeds the allowed limit.");
                }
                if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                    throw new IllegalArgumentException("Invalid image format.");
                }
            }

            // Lấy Pet từ DB
            Pet pet = petRepository.findById(petDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

            // Cập nhật thông tin Pet
            pet.setName(petDTO.getName());
            pet.setDescription(petDTO.getDescription());
            pet.setPetType(petTypeRepository.findById(petDTO.getPetTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet type not found")));

            // Nếu có file mới thì lưu file mới và cập nhật URL
            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/pets";
                String fileUrl = fileService.uploadFile(file, uploadDir);
                pet.setAvatarUrl(fileUrl);
            }

            // Lưu Pet vào DB
            petRepository.save(pet);

            // Cập nhật thông tin PetHealth
            PetHealth petHealth = petHealthRepository.findByPetId(pet.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet health not found"));
            petHealth.setWeight(petDTO.getWeight());
            petHealth.setHeight(petDTO.getHeight());
            petHealthRepository.save(petHealth);

            // Nếu có file mới thì lưu file mới và cập nhật URL
            if (file != null && !file.isEmpty()) {
                PetPhoto petPhoto = petPhotoRepository.findByPetId(pet.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Pet photo not found"));
                petPhoto.setUrl(fileService.uploadFile(file, "uploads/pets"));
                petPhotoRepository.save(petPhoto);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to edit pet: " + e.getMessage());
        }
    }

    // deletePet(user, petDTO);
    @Transactional
    public void deletePet(User user, PetDTO petDTO) {
        try {
            // Lấy Pet từ DB
            Pet pet = petRepository.findById(petDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

            // Kiểm tra xem user có quyền xóa pet không
            if (!pet.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("You do not have permission to delete this pet");
            }

            // Xóa Pet
            pet.softDelete();
            petRepository.save(pet);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete pet: " + e.getMessage());
        }
    }

    public boolean checkPetNameExists(Long id, String name, Long long1) {
        return petRepository.existsByNameAndUserIdAndDeletedFalse(name, id);
    }

    // Phương thức phân trang pets
    @Transactional
    public Page<PetDTO> retrieveAllPets(Pageable pageable) {
        Page<Pet> pets = petRepository.findAll(pageable);
        return pets.map(pet -> new PetDTO(pet));
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
