package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.Order;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId; // ID của người dùng
    private String goodsType; // Enum dưới dạng String: SPA, SHOP, HOTEL
    private Double totalPrice;
    private String status; // Trạng thái đơn hàng
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PaymentStatusDTO> paymentStatuses; // Lịch sử thanh toán
    private List<DeliveryStatusDTO> deliveryStatuses; // Lịch sử giao hàng

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.goodsType = order.getGoodsType().name();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus().name();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}