package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.entity.OrderProduct;
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long>{}