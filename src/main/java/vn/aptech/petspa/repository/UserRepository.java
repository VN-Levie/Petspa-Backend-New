package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.micrometer.common.lang.NonNull;
import vn.aptech.petspa.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Param("email") @NonNull String email);

    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id = :id")
    void softDeleteById(@Param("id") Long id);
}
