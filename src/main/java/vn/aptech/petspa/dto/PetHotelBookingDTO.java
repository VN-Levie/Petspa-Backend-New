package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelBookingDTO {
    private Long id; // ID booking
    private Long roomId; // ID phòng
    private Long petId; // ID thú cưng
    private LocalDateTime checkInTime; // Thời gian check-in
    private LocalDateTime checkOutTime; // Thời gian check-out
    private String status; // Trạng thái booking
}
