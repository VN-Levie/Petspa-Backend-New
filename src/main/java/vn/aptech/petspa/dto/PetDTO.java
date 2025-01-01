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
        PetHealth firstHealth = pet.getHealths().iterator().next();
        this.height = firstHealth.getHeight();
        this.weight = firstHealth.getWeight();
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

    public PetDTO() {
        // No-args constructor
    }
}
