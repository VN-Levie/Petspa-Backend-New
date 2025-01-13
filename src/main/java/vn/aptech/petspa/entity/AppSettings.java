package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.dto.SpaServiceScheduleDTO;
import vn.aptech.petspa.util.SpaProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings extends BaseEntity {
    @Column(name = "setting_key", nullable = false, unique = true)
    private String key; // Khóa định danh: "weeklyRestDays", "specificRestDays"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value; // Giá trị lưu dưới dạng JSON
}
