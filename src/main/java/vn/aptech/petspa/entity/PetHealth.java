package vn.aptech.petspa.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHealth extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    private Double weight; // Cân nặng (kg)

    private Double height; // Chiều cao (cm)

    private Double temperature; // Nhiệt độ cơ thể (°C)

    private Integer heartRate; // Nhịp tim (bpm)

    private String note = ""; // Ghi chú

}
