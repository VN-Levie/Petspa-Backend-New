package vn.aptech.petspa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.petspa.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
