package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.util.GoodsType;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
