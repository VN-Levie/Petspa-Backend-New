package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shop_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ShopCategory extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    // Liên kết với bảng Product
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShopProduct> products;

    // Constructor với hai tham số (name, description)
    public ShopCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
