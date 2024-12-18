package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.micrometer.common.lang.NonNull;
import vn.aptech.petspa.entity.PetHealth;
import vn.aptech.petspa.entity.PetPhoto;
import vn.aptech.petspa.entity.User;

import java.util.List;
import java.util.Optional;

public interface PetHealthRepository extends JpaRepository<PetHealth, Long> {

    List<PetHealth> findByPetId(Long id);

}
