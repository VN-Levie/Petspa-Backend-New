package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.util.GoodsType;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderRequestDTO {
    private String address;
    private Double latitude;
    private Double longitude;
    private String paymentMethod; // Enum nếu có thể: COD, VNPAY, ...
    private List<CartItemDTO> cart;
    private Double shippingFee;
    private Double total;
    private Double subtotal;
    private String name;
    private String phone;
    private GoodsType goodsType; // spa, shop, hotel

    public Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setFreeformAddress(dto.getAddress());
        order.setTotalPrice(dto.getTotal());
        order.setName(dto.getName());
        order.setPhone(dto.getPhone());
        return order;
    }
}
