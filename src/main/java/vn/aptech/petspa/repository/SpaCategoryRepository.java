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

@Repository
public interface SpaCategoryRepository extends JpaRepository<SpaCategory, Long> {
    Optional<SpaCategory> findByName(String name);

    SpaCategory findById(long id);

    @Modifying
    @Transactional
    @Query("UPDATE SpaCategory p SET p.deleted = true WHERE p.id = :id")
    void softDelete(Long id);

    // count
    @Query("SELECT COUNT(p) FROM SpaCategory p WHERE p.deleted = false")
    Long countAll();

    // countProductByCategory
    @Query("SELECT COUNT(p) FROM SpaProduct p WHERE p.category.id = :categoryId AND p.deleted = false")
    Long countProductByCategory(Long categoryId);

    // findAllProductsByCategory
    @Query("SELECT p FROM SpaProduct p WHERE p.category.id = :categoryId AND p.deleted = false")
    List<SpaProductDTO> findAllProductsByCategory(Long categoryId);
}
