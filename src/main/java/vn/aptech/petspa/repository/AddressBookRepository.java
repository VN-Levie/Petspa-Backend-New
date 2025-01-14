package vn.aptech.petspa.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.aptech.petspa.dto.AddressBookDTO;
import vn.aptech.petspa.entity.AddressBook;
import vn.aptech.petspa.dto.SpaProductDTO;

import vn.aptech.petspa.entity.SpaCategory;
import vn.aptech.petspa.entity.SpaProduct;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {

    @Query("SELECT new vn.aptech.petspa.dto.AddressBookDTO(a) FROM AddressBook a WHERE a.user.id = :id")
    Page<AddressBookDTO> findByUserId(Long id, Pageable pageable);

    AddressBook findByUserIdAndFreeformAddress(Long userId, String freeformAddress);

}
