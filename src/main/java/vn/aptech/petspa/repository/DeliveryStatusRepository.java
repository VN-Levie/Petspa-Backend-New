package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.entity.DeliveryStatus;
import vn.aptech.petspa.util.DeliveryStatusType;

import java.util.List;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {

}