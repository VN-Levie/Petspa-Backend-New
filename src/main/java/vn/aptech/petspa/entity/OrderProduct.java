package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.util.GoodsType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer productId; // spa, shop, hotel

    @Enumerated(EnumType.STRING)
    @Column(name = "goods_type", nullable = false)
    private GoodsType goodsType; // spa, shop, hotel

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "note", nullable = true)
    private String note;
}
