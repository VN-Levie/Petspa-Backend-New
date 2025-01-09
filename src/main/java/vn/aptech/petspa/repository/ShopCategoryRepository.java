package vn.aptech.petspa.repository;

import vn.aptech.petspa.dto.ShopCategoryDTO;
import vn.aptech.petspa.entity.ShopCategory;
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
public interface ShopCategoryRepository extends JpaRepository<ShopCategory, Long> {
    Optional<ShopCategory> findByName(String name);

    ShopCategory findById(long id);

    @Modifying
    @Transactional
    @Query("UPDATE ShopCategory p SET p.deleted = true WHERE p.id = :id")
    void softDelete(Long id);

    @Query("SELECT new vn.aptech.petspa.dto.ShopCategoryDTO(p.id, p.name, p.description) FROM ShopCategory p WHERE p.deleted = false")
    List<ShopCategoryDTO> findAllUndeleted();
}
