package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", insertable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Phương thức để thực hiện xóa mềm
    public void softDelete() {
        this.deleted = true;
        this.onUpdate();
    }
}
