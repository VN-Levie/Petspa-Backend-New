package vn.aptech.petspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import vn.aptech.petspa.dto.PetDTO;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.PetType;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByUserId(Long id);

    @EntityGraph(attributePaths = { "petType", "photos", "healths" })
    List<Pet> findByUserIdAndDeletedFalse(Long userId);

    boolean existsByNameAndUserIdAndDeletedFalse(String name, Long id);

    @NonNull
    Page<Pet> findAll(@NonNull Pageable pageable);

    Page<Pet> findByPetType_NameAndDeletedFalse(String type, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.PetDTO(p, h) " +
            "FROM Pet p " +
            "JOIN p.healths h " +
            "WHERE p.user.id = :userId AND p.deleted = false " +
            "AND h.updatedAt = (SELECT MAX(h2.updatedAt) FROM PetHealth h2 WHERE h2.pet.id = p.id AND h2.deleted = false)")
    List<PetDTO> findPetsWithHealths(@Param("userId") Long userId);

    Long countByUserIdAndDeletedFalse(Long userId);

    Optional<Pet> findByNameAndUserId(String name, Long id);
}
