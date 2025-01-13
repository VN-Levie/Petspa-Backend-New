package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.SpaScheduleDetails;
import vn.aptech.petspa.entity.SpaServiceSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaServiceScheduleDTO {

    private Long id; // ID của lịch dịch vụ
    private LocalDate date; // Ngày áp dụng lịch
    private LocalTime startTime; // Giờ bắt đầu
    private LocalTime endTime; // Giờ kết thúc
    private Integer maxSlot; // Số slot tối đa trong khung giờ
    private Integer bookedSlot; // Số slot đã được đặt

    public SpaServiceSchedule toEntity() {
        SpaServiceSchedule schedule = new SpaServiceSchedule();
        schedule.setId(this.getId());
        schedule.setDate(this.getDate());
        schedule.setScheduleDetails(new SpaScheduleDetails());
        schedule.getScheduleDetails().setStartTime(this.getStartTime());
        schedule.getScheduleDetails().setEndTime(this.getEndTime());
        schedule.getScheduleDetails().setMaxSlot(this.getMaxSlot());
        schedule.getScheduleDetails().setBookedSlot(this.getBookedSlot());
        return schedule;
    }

}
