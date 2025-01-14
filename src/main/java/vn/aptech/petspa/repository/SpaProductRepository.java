package vn.aptech.petspa.repository;

import vn.aptech.petspa.dto.SpaProductDTO;

import vn.aptech.petspa.entity.SpaCategory;
import vn.aptech.petspa.entity.SpaProduct;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface SpaProductRepository extends JpaRepository<SpaProduct, Long> {
    Optional<SpaCategory> findByName(String name);

    SpaCategory findById(long id);

    // count
    @Query("SELECT COUNT(p) FROM SpaCategory p WHERE p.deleted = false")
    Long countAll();

    // countProductByCategory
    @Query("SELECT COUNT(p) FROM SpaProduct p WHERE p.category.id = :categoryId AND p.deleted = false")
    Long countProductByCategory(Long categoryId);

    // findAllProductsByCategory
    @Query("SELECT p FROM SpaProduct p WHERE p.category.id = :categoryId AND p.deleted = false")
    List<SpaProduct> findAllProductsByCategory(Long categoryId);

    // Long id, String name, BigDecimal price, String imageUrl, Long category,
    // String description
    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p.id, p.name, p.price, p.imageUrl, p.category.id, p.description) FROM SpaProduct p WHERE p.category.id = :id AND p.deleted = false")
    Optional<List<SpaProductDTO>> findByCategoryId(Long id);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p.id, p.name, p.price, p.imageUrl, p.category.id, p.description) FROM SpaProduct p WHERE p.id = :id AND p.deleted = false")
    Optional<SpaProductDTO> findByIdAndDeletedFalse(Long id);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p) FROM SpaProduct p WHERE p.name LIKE CONCAT('%', :name, '%') AND p.deleted = false")
    Page<SpaProductDTO> findByName(String name, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p) FROM SpaProduct p WHERE p.category.id = :categoryId AND p.deleted = false")
    Page<SpaProductDTO> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p) FROM SpaProduct p WHERE p.name LIKE CONCAT('%', :name, '%') AND p.category.id = :categoryId AND p.deleted = false")
    Page<SpaProductDTO> findByNameAndCategoryId(String name, Long categoryId, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p) FROM SpaProduct p WHERE p.deleted = false")
    Page<SpaProductDTO> findAllUndeleted(Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p) FROM SpaProduct p WHERE p.deleted = true")
    Page<SpaProductDTO> findAllDeleted(Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.SpaProductDTO(p) FROM SpaProduct p")
    Page<SpaProductDTO> findAllAdmin(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE SpaProduct p SET p.deleted = true WHERE p.id = :id")
    void softDelete(Long id);
}
