package vn.aptech.petspa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.dto.OrderDTO;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.util.GoodsType;
import vn.aptech.petspa.util.OrderStatusType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        @Query("""
                        SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                        FROM Order o
                        WHERE o.deleted = false
                          AND (:userId IS NULL OR o.user.id = :userId)
                          AND (:search IS NULL OR o.goodsType LIKE %:search% OR o.status LIKE %:search%)
                          AND (:goodsType IS NULL OR o.goodsType = :goodsType)
                          AND (:date IS NULL OR FUNCTION('DATE', o.createdAt) = :date)
                        """)
        Page<OrderDTO> findByUserIdAndSearchAndGoodsTypeAndDate(
                        @Param("userId") Long userId,
                        @Param("search") String search,
                        @Param("goodsType") GoodsType goodsType,
                        @Param("date") String date,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                              AND (:search IS NULL OR o.goodsType LIKE %:search% OR o.status LIKE %:search%)
                              AND (:goodsType IS NULL OR o.goodsType = :goodsType)
                        """)
        Page<OrderDTO> findByUserIdAndSearchAndGoodsType(
                        @Param("userId") Long userId,
                        @Param("search") String search,
                        @Param("goodsType") GoodsType goodsType,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                              AND (:search IS NULL OR o.goodsType LIKE %:search% OR o.status LIKE %:search%)
                              AND (:date IS NULL OR FUNCTION('DATE', o.createdAt) = :date)
                        """)
        Page<OrderDTO> findByUserIdAndSearchAndDate(
                        @Param("userId") Long userId,
                        @Param("search") String search,
                        @Param("date") String date,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                              AND (:goodsType IS NULL OR o.goodsType = :goodsType)
                              AND (:date IS NULL OR FUNCTION('DATE', o.createdAt) = :date)
                        """)
        Page<OrderDTO> findByUserIdAndGoodsTypeAndDate(
                        @Param("userId") Long userId,
                        @Param("goodsType") GoodsType goodsType,
                        @Param("date") String date,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                              AND (:search IS NULL OR o.goodsType LIKE %:search% OR o.status LIKE %:search%)
                        """)
        Page<OrderDTO> findByUserIdAndSearch(
                        @Param("userId") Long userId,
                        @Param("search") String search,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                              AND (:goodsType IS NULL OR o.goodsType = :goodsType)
                        """)
        Page<OrderDTO> findByUserIdAndGoodsType(
                        @Param("userId") Long userId,
                        @Param("goodsType") GoodsType goodsType,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                              AND (:date IS NULL OR FUNCTION('DATE', o.createdAt) = :date)
                        """)
        Page<OrderDTO> findByUserIdAndDate(
                        @Param("userId") Long userId,
                        @Param("date") String date,
                        Pageable pageable);

        @Query("""
                            SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                            FROM Order o
                            WHERE o.deleted = false
                              AND o.user.id = :userId
                        """)
        Page<OrderDTO> findByUserId(
                        @Param("userId") Long userId,
                        Pageable pageable);

        List<Order> findByPetAndStatus(Pet pet, OrderStatusType pending);

        List<Order> findByPetAndStatusAndGoodsType(Pet pet, OrderStatusType pending, GoodsType spa);

        @Query("SELECT o FROM Order o " +
                        "JOIN o.orderProducts op " +
                        "WHERE o.pet.id = :petId " +
                        "AND o.status = :status " +
                        "AND o.goodsType = :goodsType " +
                        "AND o.date BETWEEN :startDate AND :endDate " +
                        "AND op.id IN :productIds")
        List<Order> findConflictingOrders(
                        @Param("petId") Long petId,
                        @Param("status") OrderStatusType status,
                        @Param("goodsType") GoodsType goodsType,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("productIds") Set<Long> productIds);

}
