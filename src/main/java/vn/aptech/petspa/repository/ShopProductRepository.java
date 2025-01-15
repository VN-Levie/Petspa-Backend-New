package vn.aptech.petspa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.aptech.petspa.dto.ShopProductDTO;
import vn.aptech.petspa.entity.ShopProduct;

@Repository
public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p) FROM ShopProduct p WHERE p.name LIKE CONCAT('%', :name, '%') AND p.deleted = :deleted")
    Page<ShopProductDTO> findByName(@Param("name") String name, Boolean deleted, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p) FROM ShopProduct p WHERE p.category.id = :categoryId AND p.deleted = :deleted")
    Page<ShopProductDTO> findByCategoryId(Long categoryId, Boolean deleted, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p) FROM ShopProduct p WHERE p.name LIKE CONCAT('%', :name, '%') AND p.category.id = :categoryId AND p.deleted = :deleted")
    Page<ShopProductDTO> findByNameAndCategoryId(@Param("name") String name, Long categoryId, Boolean deleted,
            Pageable pageable);

    // find all undeleted products
    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p) FROM ShopProduct p WHERE p.deleted = false")
    Page<ShopProductDTO> findAllUndeleted(Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p) FROM ShopProduct p WHERE p.deleted = true")
    Page<ShopProductDTO> findAllDeleted(Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p) FROM ShopProduct p")
    Page<ShopProductDTO> findAllAdmin(Pageable pageable);

    // soft delete
    @Modifying
    @Transactional
    @Query("UPDATE ShopProduct p SET p.deleted = true WHERE p.id = :id")
    void softDelete(Long id);

    // undelete
    @Modifying
    @Transactional
    @Query("UPDATE ShopProduct p SET p.deleted = false WHERE p.id = :id")
    void unDelete(Long id);
}
