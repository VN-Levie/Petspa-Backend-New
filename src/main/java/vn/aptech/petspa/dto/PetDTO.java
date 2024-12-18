package vn.aptech.petspa.dto;

import lombok.Data;
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
}
