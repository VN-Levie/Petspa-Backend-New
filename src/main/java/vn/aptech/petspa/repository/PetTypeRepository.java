package vn.aptech.petspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.PetType;

public interface PetTypeRepository extends JpaRepository<PetType, Long> {

}
