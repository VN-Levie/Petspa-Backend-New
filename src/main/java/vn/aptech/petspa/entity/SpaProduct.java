package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.dto.SpaServiceScheduleDTO;
import vn.aptech.petspa.util.SpaProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "spa_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaProduct extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SpaProductStatus status = SpaProductStatus.ACTIVE;

    // slot trống
    @Column(nullable = true)
    private Integer slotRequired = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private SpaCategory category;

 
    

}
