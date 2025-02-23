package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "pet_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetPhoto extends BaseEntity {

    // Liên kết thú cưng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Pet pet;

    // URL ảnh
    @Column(name = "photo_url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
}
