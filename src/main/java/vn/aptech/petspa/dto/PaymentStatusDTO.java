package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class PaymentStatusDTO {
    private Long id;
    private Long orderId; // ID của Order liên kết
    private String paymentType; // Enum dưới dạng String: COD, VNPAY, PAYPAL, CREDIT_CARD
    private String status; // Enum dưới dạng String: PENDING, COMPLETED, FAILED
    private Date date; // Thời gian cập nhật trạng thái
}