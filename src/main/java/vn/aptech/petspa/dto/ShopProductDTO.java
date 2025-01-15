package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.ShopProduct;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductDTO {

    private Long id;
    private String name;
    private Double price;
    private String description;
    private Long categoryId;
    private String imageUrl;
    private Integer quantity;
    private Boolean isDeleted = false;

    public ShopProductDTO(ShopProduct shopProduct) {
        this.id = shopProduct.getId();
        this.name = shopProduct.getName();
        this.price = shopProduct.getPrice();
        this.description = shopProduct.getDescription();
        this.categoryId = shopProduct.getCategory().getId();
        this.imageUrl = shopProduct.getImageUrl();
        this.quantity = shopProduct.getQuantity();
        this.isDeleted = shopProduct.getDeleted();
    }

}
