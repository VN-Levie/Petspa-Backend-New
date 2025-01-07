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

    @Query("SELECT new vn.aptech.petspa.dto.PetDTO(p.id, p.name, p.description, ph.height, ph.weight, " +
            "p.user.id, p.avatarUrl, p.petType.id) " +
            "FROM Pet p " +
            "LEFT JOIN p.healths ph " +
            "WHERE p.user.id = :userId AND NOT p.deleted " +
            "AND ph.id = (SELECT MAX(ph2.id) FROM PetHealth ph2 WHERE ph2.pet.id = p.id)")
    Page<PetDTO> findUserPets(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.PetDTO(p.id, p.name, p.description, ph.height, ph.weight, " +
            "p.user.id, p.avatarUrl, p.petType.id) " +
            "FROM Pet p " +
            "LEFT JOIN p.healths ph " +
            "WHERE p.user.id = :userId AND p.name LIKE %:name% AND NOT p.deleted " +
            "AND ph.id = (SELECT MAX(ph2.id) FROM PetHealth ph2 WHERE ph2.pet.id = p.id)")
    Page<PetDTO> findUserPetsByName(@Param("userId") Long userId,
            @Param("name") String name,
            Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.PetDTO(p.id, p.name, p.description, ph.height, ph.weight, " +
            "p.user.id, p.avatarUrl, p.petType.id) " +
            "FROM Pet p " +
            "LEFT JOIN p.healths ph " +
            "WHERE p.user.id = :userId AND p.petType.id = :petTypeId AND NOT p.deleted " +
            "AND ph.id = (SELECT MAX(ph2.id) FROM PetHealth ph2 WHERE ph2.pet.id = p.id)")
    Page<PetDTO> findUserPetsByType(@Param("userId") Long userId,
            @Param("petTypeId") Long petTypeId,
            Pageable pageable);

    @Query("SELECT new vn.aptech.petspa.dto.PetDTO(p.id, p.name, p.description, ph.height, ph.weight, " +
            "p.user.id, p.avatarUrl, p.petType.id) " +
            "FROM Pet p " +
            "LEFT JOIN p.healths ph " +
            "WHERE p.user.id = :userId AND p.name LIKE %:name% AND p.petType.id = :petTypeId AND NOT p.deleted " +
            "AND ph.id = (SELECT MAX(ph2.id) FROM PetHealth ph2 WHERE ph2.pet.id = p.id)")
    Page<PetDTO> findUserPetsByNameAndType(@Param("userId") Long userId,
            @Param("name") String name,
            @Param("petTypeId") Long petTypeId,
            Pageable pageable);

    boolean existsByNameAndUserIdAndDeletedFalse(String name, Long id);

    @NonNull
    Page<Pet> findAll(@NonNull Pageable pageable);

    Page<Pet> findByPetType_NameAndDeletedFalse(String type, Pageable pageable);

    Long countByUserIdAndDeletedFalse(Long userId);

    boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long petId);

}
