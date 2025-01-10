package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.entity.PaymentStatus;
import vn.aptech.petspa.util.PaymentStatusType;
import vn.aptech.petspa.util.PaymentType;

import java.util.List;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {

}
