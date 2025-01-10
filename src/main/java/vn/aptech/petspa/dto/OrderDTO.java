package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Date createdAt;
    private Date updatedAt;

    private List<PaymentStatusDTO> paymentStatuses; // Lịch sử thanh toán
    private List<DeliveryStatusDTO> deliveryStatuses; // Lịch sử giao hàng
}