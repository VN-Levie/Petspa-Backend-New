package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "spa_service_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaServiceSchedule extends BaseEntity {

    // Ngày áp dụng lịch
    @Column(nullable = false)
    private LocalDate date;

    // Giờ bắt đầu và kết thúc
    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // Số slot tối đa cho khung giờ
    @Column(nullable = false)
    private Integer maxSlot;

    // Số slot đã được đặt
    @Column(nullable = false)
    private Integer bookedSlot;

    // Phương thức tiện ích (nếu cần)
    public boolean isFull() {
        return bookedSlot >= maxSlot;
    }
}
