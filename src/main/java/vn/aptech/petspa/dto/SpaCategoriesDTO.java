package vn.aptech.petspa.dto;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.SpaCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaCategoriesDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Optional<List<SpaProductDTO>> items = Optional.empty();

    public SpaCategoriesDTO(Long id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public SpaCategoriesDTO(SpaCategory spaCategory) {
        this.id = spaCategory.getId();
        this.name = spaCategory.getName();
        this.description = spaCategory.getDescription();
        this.imageUrl = spaCategory.getImageUrl();
    }
}
