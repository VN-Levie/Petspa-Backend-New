package vn.aptech.petspa.dto;

import lombok.Data;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.PetHealth;

import java.util.List;

@Data
public class PetDTO {

    private Long id;
    private String name;
    private String description;
    private Double height;
    private Double weight;
    private Long userId = -1L;
    private String avatarUrl = "";
    private Long petTypeId; // Liên kết với PetType

    public PetDTO(Pet pet) {
        this.id = pet.getId();
        this.name = pet.getName();
        this.description = pet.getDescription();
        PetHealth lastHealth = pet.getHealths()
                .stream()
                .max((h1, h2) -> h1.getId().compareTo(h2.getId()))
                .orElse(new PetHealth());
        this.height = lastHealth.getHeight();
        this.weight = lastHealth.getWeight();
        this.userId = pet.getUser().getId();
        this.avatarUrl = pet.getAvatarUrl();
        this.petTypeId = pet.getPetType().getId();
    }

    public PetDTO(Pet pet, PetHealth health) {
        this.id = pet.getId();
        this.name = pet.getName();
        this.description = pet.getDescription();
        this.height = health.getHeight();
        this.weight = health.getWeight();
        this.userId = pet.getUser().getId();
        this.avatarUrl = pet.getAvatarUrl();
        this.petTypeId = pet.getPetType().getId();
    }

    public PetDTO(Long id, String name, String description, Double height, Double weight,
                  Long userId, String avatarUrl, Long petTypeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.userId = userId;
        this.avatarUrl = avatarUrl;
        this.petTypeId = petTypeId;
    }

    public PetDTO() {
        // No-args constructor
    }
}
