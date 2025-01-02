package vn.aptech.petspa.repository;

import vn.aptech.petspa.entity.ShopProduct;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {
    List<ShopProduct> findByCategoryId(Long categoryId);

    // soft delete
    @Modifying
    @Transactional
    @Query("UPDATE ShopProduct p SET p.deleted = true WHERE p.id = :id")
    void softDelete(Long id);

}
