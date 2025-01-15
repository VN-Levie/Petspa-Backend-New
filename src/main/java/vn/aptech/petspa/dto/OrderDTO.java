package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.DeliveryStatus;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.entity.OrderProduct;
import vn.aptech.petspa.entity.PaymentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.method.P;

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
    private List<OrderProductDTO> orderProducts; // Danh sách sản phẩm trong đơn hàng

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.goodsType = order.getGoodsType().name();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus().name();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        
        if (order.getPaymentStatuses() != null) {
            this.paymentStatuses = new ArrayList<>();
            for (PaymentStatus paymentStatus : order.getPaymentStatuses()) {
                this.paymentStatuses.add(new PaymentStatusDTO(paymentStatus));
            }
        }

        if (order.getDeliveryStatuses() != null) {
            this.deliveryStatuses = new ArrayList<>();
            for (DeliveryStatus deliveryStatus : order.getDeliveryStatuses()) {
                this.deliveryStatuses.add(new DeliveryStatusDTO(deliveryStatus));
            }
        }


        if (order.getOrderProducts() != null) {
            this.orderProducts = new ArrayList<>();
            for (OrderProduct orderProduct : order.getOrderProducts()) {
                this.orderProducts.add(new OrderProductDTO(orderProduct));
            }

        }

    }
}