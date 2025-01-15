package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vn.aptech.petspa.util.DeliveryStatusType;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_delivery_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatus extends BaseEntity {
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatusType status; // pending, shipped, delivered

    @Column(name = "note")
    private String note;

}
