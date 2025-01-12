package vn.aptech.petspa.dto;

import java.util.List;

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

    // toJsonString
    public String toJsonString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"" + name + "\"," +
                "\"price\":" + price + "," +
                "\"description\":\"" + description + "\"," +
                "\"categoryId\":" + categoryId + "," +
                "\"imageUrl\":\"" + imageUrl + "\"," +
                "\"quantity\":" + quantity +
                "}";
    }

    public static String toJsonArray(List<CartItemDTO> cart) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < cart.size(); i++) {
            sb.append(cart.get(i).toJsonString());
            if (i < cart.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
