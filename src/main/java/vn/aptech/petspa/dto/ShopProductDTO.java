package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Long categoryId;
    private String imageUrl;

}
