package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.SpaProduct;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private Long category;
    private String description;
    private Integer slotRequired;
    private boolean deleted;

    public SpaProductDTO(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public SpaProductDTO(Long id, String name, Double price, String imageUrl, Long category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
    }

    public SpaProductDTO(SpaProduct spaProduct) {
        this.id = spaProduct.getId();
        this.name = spaProduct.getName();
        this.price = spaProduct.getPrice();
        this.imageUrl = spaProduct.getImageUrl();
        this.category = spaProduct.getCategory().getId();
        this.description = spaProduct.getDescription();
        this.slotRequired = spaProduct.getSlotRequired();
        this.deleted = spaProduct.getDeleted();
    }

}
