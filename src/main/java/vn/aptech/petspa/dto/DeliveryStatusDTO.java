package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class DeliveryStatusDTO {
    public DeliveryStatusDTO(DeliveryStatus deliveryStatus) {
        this.id = deliveryStatus.getId();
        this.orderId = deliveryStatus.getOrder().getId();
        this.status = deliveryStatus.getStatus().name();
        this.note = deliveryStatus.getNote();
        this.date = deliveryStatus.getCreatedAt();
    }

    private Long id;
    private Long orderId; // ID của Order liên kết
    private String status; // Enum dưới dạng String: PENDING, ON_THE_WAY, PICKED_UP, ARRIVED_AT_SHOP, ...
    private String note; // Ghi chú trạng thái
    private LocalDateTime date; // Thời gian cập nhật trạng thái
}