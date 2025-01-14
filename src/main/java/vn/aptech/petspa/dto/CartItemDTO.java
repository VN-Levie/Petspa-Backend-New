package vn.aptech.petspa.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Integer quantity = 1;

}
