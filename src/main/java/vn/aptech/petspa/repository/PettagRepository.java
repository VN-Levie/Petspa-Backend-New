package vn.aptech.petspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.petspa.entity.PetPhoto;
import vn.aptech.petspa.entity.PetTag;

public interface PettagRepository extends JpaRepository<PetTag, Long> {

    Optional<PetTag> findByPetId(Long id);

}
