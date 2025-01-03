package vn.aptech.petspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.petspa.entity.PetHealth;

public interface PetHealthRepository extends JpaRepository<PetHealth, Long> {

    Optional<PetHealth> findByPetId(Long petId);

    Optional<PetHealth> findTopByPetIdOrderByIdDesc(Long petId);

}
