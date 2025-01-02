package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Long category;
    private String description;
    private boolean deleted;

    public SpaProductDTO(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public SpaProductDTO(Long id, String name, BigDecimal price, String imageUrl, Long category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
    }

}
