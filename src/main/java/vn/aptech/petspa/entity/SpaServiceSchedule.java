package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.dto.SpaServiceScheduleDTO;
import vn.aptech.petspa.util.ScheduleDetailsConverter;

import java.time.LocalDate;

@Entity
@Table(name = "spa_service_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaServiceSchedule extends BaseEntity {

    // Ngày áp dụng lịch
    @Column(nullable = false)
    private LocalDate date;

    // Lưu trữ dữ liệu JSON cho các khung giờ
    @Column(columnDefinition = "TEXT", nullable = false)
    @Convert(converter = ScheduleDetailsConverter.class)
    private SpaScheduleDetail scheduleDetails;

    // Phương thức tiện ích
    public boolean isFull() {
        return scheduleDetails != null && scheduleDetails.getBookedSlot() >= scheduleDetails.getMaxSlot();
    }

    public SpaServiceScheduleDTO toDto() {
        return new SpaServiceScheduleDTO(
                this.getId(),
                this.getDate(),
                scheduleDetails.getStartTime(),
                scheduleDetails.getEndTime(),
                scheduleDetails.getMaxSlot(),
                scheduleDetails.getBookedSlot());
    }
}
