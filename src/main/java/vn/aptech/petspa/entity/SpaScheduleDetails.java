package vn.aptech.petspa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaScheduleDetails {

    private LocalTime startTime; // Giờ bắt đầu
    private LocalTime endTime; // Giờ kết thúc
    private Integer maxSlot; // Số slot tối đa
    private Integer bookedSlot; // Số slot đã được đặt
}
