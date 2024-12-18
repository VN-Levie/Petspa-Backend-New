package vn.aptech.petspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.petspa.entity.PetHealth;

public interface PetHealthRepository extends JpaRepository<PetHealth, Long> {

    List<PetHealth> findByPetId(Long id);

}
