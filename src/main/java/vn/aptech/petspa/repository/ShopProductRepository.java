package vn.aptech.petspa.repository;

import vn.aptech.petspa.dto.ShopProductDTO;
import vn.aptech.petspa.entity.ShopProduct;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.lang.NonNull;
@Repository
public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    
    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p.id, p.name, p.price, p.description, p.category.id, p.avatarUrl) FROM ShopProduct p WHERE p.deleted = false")
    List<ShopProductDTO> findByCategoryId(Long categoryId);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p.id, p.name, p.price, p.description, p.category.id, p.avatarUrl) FROM ShopProduct p WHERE p.deleted = false")
    Page<ShopProductDTO> findByName(String name, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p.id, p.name, p.price, p.description, p.category.id, p.avatarUrl) FROM ShopProduct p WHERE p.deleted = false")
    Page<ShopProductDTO> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p.id, p.name, p.price, p.description, p.category.id, p.avatarUrl) FROM ShopProduct p WHERE p.deleted = false")
    Page<ShopProductDTO> findByNameAndCategoryId(String name, Long categoryId, Pageable pageable);

    //find all undeleted products
    @Query("SELECT new vn.aptech.petspa.dto.ShopProductDTO(p.id, p.name, p.price, p.description, p.category.id, p.avatarUrl) FROM ShopProduct p WHERE p.deleted = false")
    Page<ShopProductDTO> findAllUndeleted(Pageable pageable);

    // soft delete
    @Modifying
    @Transactional
    @Query("UPDATE ShopProduct p SET p.deleted = true WHERE p.id = :id")
    void softDelete(Long id);
}
