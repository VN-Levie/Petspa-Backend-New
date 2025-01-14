package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class DeliveryStatusDTO {
    private Long id;
    private Long orderId; // ID của Order liên kết
    private String status; // Enum dưới dạng String: PENDING, ON_THE_WAY, PICKED_UP, ARRIVED_AT_SHOP, ...
    private String note; // Ghi chú trạng thái
    private Date date; // Thời gian cập nhật trạng thái
}