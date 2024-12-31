package vn.aptech.petspa.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.petspa.entity.PetPhoto;
public interface PetPhotoRepository extends JpaRepository<PetPhoto, Long> {

    Optional<PetPhoto> findByPetId(Long id);

}
