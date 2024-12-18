package vn.aptech.petspa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.aptech.petspa.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByUserId(Long id);
    
    
    @EntityGraph(attributePaths = {"petType", "photos", "healths"})
    List<Pet> findByUserIdAndDeletedFalse(Long userId);
}
