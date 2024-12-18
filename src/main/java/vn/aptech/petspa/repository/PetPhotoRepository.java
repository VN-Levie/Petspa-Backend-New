package vn.aptech.petspa.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.micrometer.common.lang.NonNull;
import vn.aptech.petspa.entity.PetPhoto;
import vn.aptech.petspa.entity.User;

import java.util.List;
import java.util.Optional;
public interface PetPhotoRepository extends JpaRepository<PetPhoto, Long> {

    List<PetPhoto> findByPetId(Long id);

}
