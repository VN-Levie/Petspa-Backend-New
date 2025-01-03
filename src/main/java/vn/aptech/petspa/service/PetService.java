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
import vn.aptech.petspa.util.ZDebug;

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

    @Transactional(readOnly = true)
    public Page<PetDTO> getUserPets(Long userId, String name, Long petTypeId, Pageable pageable) {
        Page<Pet> pets;
        if (name != null && petTypeId != null) {
            pets = petRepository.findByUserIdAndNameContainingAndPetTypeIdAndDeletedFalse(userId, name, petTypeId,
                    pageable);
        } else if (name != null) {
            pets = petRepository.findByUserIdAndNameContainingAndDeletedFalse(userId, name, pageable);
        } else if (petTypeId != null) {
            pets = petRepository.findByUserIdAndPetTypeIdAndDeletedFalse(userId, petTypeId, pageable);
        } else {
            pets = petRepository.findByUserIdAndDeletedFalse(userId, pageable);
        }
        return pets.map(pet -> new PetDTO(pet));
    }

    public List<Pet> retrievePetsForAdmin(Long userId) {
        // Trả về tất cả Pet của user (bao gồm deleted)
        return petRepository.findByUserId(userId);
    }

    private void checkPetValid(PetDTO petDTO) {
        if (petDTO.getWeight() < 0 || petDTO.getHeight() < 0) {
            throw new IllegalArgumentException("Weight and height must be greater than 0.");
        }
        if (petDTO.getHeight() > 200) {
            throw new IllegalArgumentException("Height must be less than 200 cm.\n We accept pets, not dinosaurs!?");
        }
        if (petDTO.getWeight() > 120) {
            throw new IllegalArgumentException(
                    "Weight limit: 120 kg.\n We love big pets... just not bear-sized ones!");
        }
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

            checkPetValid(petDTO);
            boolean petNameExists = petRepository.existsByNameAndUserIdAndIdNot(petDTO.getName(), user.getId(),
                    petDTO.getId());

            if (petNameExists) {
                throw new IllegalArgumentException("Pet name already exists");
            }
            // Lưu file và lấy URL
            String uploadDir = "pets";
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
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // editPet | petService.editPet(user, petDTO, file);
    @Transactional
    public void editPet(User user, PetDTO petDTO, MultipartFile file) {
        try {
            // Lấy Pet từ DB
            Pet pet = petRepository.findById(petDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

            // Kiểm tra trùng tên (trừ trường hợp cùng id)
            // boolean petNameExists = petRepository.findByNameAndUserId(petDTO.getName(),
            // user.getId())
            // .filter(existingPet -> !existingPet.getId().equals(pet.getId()))
            // .isPresent();

            boolean petNameExists = petRepository.existsByNameAndUserIdAndIdNot(petDTO.getName(), user.getId(),
                    petDTO.getId());

            if (petNameExists) {
                throw new IllegalArgumentException("Pet name already exists");
            }
            checkPetValid(petDTO);
            // Cập nhật thông tin Pet
            pet.setName(petDTO.getName());
            pet.setDescription(petDTO.getDescription());
            pet.setPetType(petTypeRepository.findById(petDTO.getPetTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet type not found")));

            // Nếu có file mới thì xử lý và cập nhật avatar
            if (file != null && !file.isEmpty()) {
                if (!fileService.isImageSize(file.getSize())) {
                    throw new IllegalArgumentException("File size exceeds the allowed limit.");
                }
                if (!fileService.isImage(file.getInputStream(), file.getOriginalFilename())) {
                    throw new IllegalArgumentException("Invalid image format.");
                }
                String uploadDir = "pets";
                String fileUrl = fileService.uploadFile(file, uploadDir);
                pet.setAvatarUrl(fileUrl); // Cập nhật URL avatar mới
            }

            // Lưu Pet vào DB
            petRepository.save(pet);

            // Cập nhật thông tin PetHealth
            PetHealth existingPetHealth = petHealthRepository.findTopByPetIdOrderByIdDesc(pet.getId()).orElse(null);

            if (existingPetHealth == null) {
                // Tạo PetHealth mới nếu không tồn tại thông tin sức khỏe cũ
                PetHealth newPetHealth = new PetHealth();
                newPetHealth.setPet(pet);
                newPetHealth.setWeight(petDTO.getWeight());
                newPetHealth.setHeight(petDTO.getHeight());
                petHealthRepository.save(newPetHealth);
            } else {
                // Kiểm tra nếu thông tin sức khỏe mới khác thông tin sức khỏe cũ
                if (!existingPetHealth.getWeight().equals(petDTO.getWeight()) ||
                        !existingPetHealth.getHeight().equals(petDTO.getHeight())) {
                    PetHealth newPetHealth = new PetHealth();
                    newPetHealth.setPet(pet);
                    newPetHealth.setWeight(petDTO.getWeight());
                    newPetHealth.setHeight(petDTO.getHeight());
                    petHealthRepository.save(newPetHealth);
                }
            }

            // Nếu có file mới thì cập nhật PetPhoto
            if (file != null && !file.isEmpty()) {

                PetPhoto newPetPhoto = new PetPhoto();
                newPetPhoto.setPet(pet);
                newPetPhoto.setUrl(pet.getAvatarUrl());
                newPetPhoto.setUploadedBy(user);
                petPhotoRepository.save(newPetPhoto);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            ZDebug.gI().logException("Failed to edit pet: " + e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage());
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

    public Long countUserPet(Long userId) {
        // count pet
        return petRepository.countByUserIdAndDeletedFalse(userId);
    }

    public void editPetWithAvatar(User user, PetDTO petDTO, MultipartFile file) {
        System.out.println("Edit pet with avatar");
        // edit pet with avatar
        editPet(user, petDTO, file);
    }

    public void editPetWithoutAvatar(User user, PetDTO petDTO) {
        System.out.println("Edit pet without avatar");
        // edit pet without avatar
        editPet(user, petDTO, null);
    }
}
