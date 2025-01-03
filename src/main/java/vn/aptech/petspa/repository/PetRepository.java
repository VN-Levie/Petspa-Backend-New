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

    @EntityGraph(attributePaths = { "petType", "photos", "healths" })
    Page<Pet> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "petType", "photos", "healths" })
    Page<Pet> findByUserIdAndNameContainingAndPetTypeIdAndDeletedFalse(Long userId, String name, Long petTypeId,
            Pageable pageable);

    @EntityGraph(attributePaths = { "petType", "photos", "healths" })
    Page<Pet> findByUserIdAndNameContainingAndDeletedFalse(Long userId, String name, Pageable pageable);

    @EntityGraph(attributePaths = { "petType", "photos", "healths" })
    Page<Pet> findByUserIdAndPetTypeIdAndDeletedFalse(Long userId, Long petTypeId, Pageable pageable);

    boolean existsByNameAndUserIdAndDeletedFalse(String name, Long id);

    @NonNull
    Page<Pet> findAll(@NonNull Pageable pageable);

    Page<Pet> findByPetType_NameAndDeletedFalse(String type, Pageable pageable);

    Long countByUserIdAndDeletedFalse(Long userId);

    boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long petId);

}
