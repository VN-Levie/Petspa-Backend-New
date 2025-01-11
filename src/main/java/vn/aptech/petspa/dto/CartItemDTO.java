package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private Long categoryId;
    private String imageUrl;
    private Integer quantity;
}
