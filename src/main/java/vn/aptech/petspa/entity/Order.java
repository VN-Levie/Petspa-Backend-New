package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vn.aptech.petspa.util.GoodsType;
import vn.aptech.petspa.util.OrderStatusType;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = true)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "goods_type", nullable = false)
    private GoodsType goodsType; // spa, shop, hotel

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusType status; // trạng thái đơn hàng (e.g., pending, completed, cancelled)

    @Column(name = "freeform_address")
    private String freeformAddress;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "date", nullable = true)
    private LocalDate date; // Ngày áp dụng lịch

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate; // Ngày check-out hotel

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime; // Giờ bắt đầu

    @Column(name = "end_time", nullable = true)
    private LocalTime endTime; // Giờ kết thúc

    // Payment Status History
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentStatus> paymentStatuses = new ArrayList<>();

    // Delivery Status History
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
}
