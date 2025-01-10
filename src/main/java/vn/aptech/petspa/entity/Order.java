package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vn.aptech.petspa.util.GoodsType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "goods_type", nullable = false)
    private GoodsType goodsType; // spa, shop, hotel

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "status", nullable = false)
    private String status; // trạng thái đơn hàng (e.g., pending, completed, cancelled)

    @Column(name = "freeform_address")
    private String freeformAddress;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    // Payment Status History
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentStatus> paymentStatuses = new ArrayList<>();

    // Delivery Status History
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
}
