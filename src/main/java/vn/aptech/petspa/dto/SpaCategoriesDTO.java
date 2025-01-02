package vn.aptech.petspa.dto;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaCategoriesDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Optional<List<SpaProductDTO>> items = Optional.empty();
}
