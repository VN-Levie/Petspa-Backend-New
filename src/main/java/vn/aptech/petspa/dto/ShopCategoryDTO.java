package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopCategoryDTO {
    private Long id;
    private String name;
    private String description;
}
