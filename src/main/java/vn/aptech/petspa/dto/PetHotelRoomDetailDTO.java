package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelRoomDetailDTO {
    private Long id; // ID chi tiết phòng
    private LocalDateTime checkInTime; // Thời gian check-in
    private LocalDateTime checkOutTime; // Thời gian check-out
    private String status; // Trạng thái phòng (BOOKED, CHECKED_IN, CHECKED_OUT, CANCELED)
    private Long petId; // ID thú cưng
}
