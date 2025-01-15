package vn.aptech.petspa.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.entity.OrderProduct;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderProductDTO {
    private Long id;
    private Long orderId;
    private Long productId; // spa, shop, hotel, pet_tag
    private String goodsType; // Enum dưới dạng String: SPA, SHOP, HOTEL
    private Double price;
    private Integer quantity = 1;
    private String note;

    public OrderProductDTO(OrderProduct orderProduct) {
        this.id = orderProduct.getId();
        this.orderId = orderProduct.getOrder().getId();
        this.productId = orderProduct.getProductId();
        this.goodsType = orderProduct.getGoodsType().name();
        this.price = orderProduct.getPrice();
        this.quantity = orderProduct.getQuantity();
        this.note = orderProduct.getNote();
    }
}
