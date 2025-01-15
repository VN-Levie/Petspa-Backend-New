package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class PaymentStatusDTO {
    
    private Long id;
    private Long orderId; // ID của Order liên kết
    private String paymentType; // Enum dưới dạng String: COD, VNPAY, PAYPAL, CREDIT_CARD
    private String status; // Enum dưới dạng String: PENDING, COMPLETED, FAILED
    private LocalDateTime date; // Thời gian cập nhật trạng thái

    public PaymentStatusDTO(PaymentStatus paymentStatus) {
        this.id = paymentStatus.getId();
        this.orderId = paymentStatus.getOrder().getId();
        this.paymentType = paymentStatus.getPaymentType().name();
        this.status = paymentStatus.getStatus().name();
        this.date = paymentStatus.getCreatedAt();
    }
}