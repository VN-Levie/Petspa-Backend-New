package vn.aptech.petspa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.aptech.petspa.dto.OrderDTO;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.util.GoodsType;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT new vn.aptech.petspa.dto.OrderDTO(o)
        FROM Order o
        WHERE o.deleted = false
          AND (:userId IS NULL OR o.userId = :userId)
          AND (:search IS NULL OR o.goodsType LIKE %:search% OR o.status LIKE %:search%)
          AND (:goodsType IS NULL OR o.goodsType = :goodsType)
          AND (:date IS NULL OR FUNCTION('DATE', o.createdAt) = :date)
        """)
    Page<OrderDTO> findByUserIdAndSearchAndGoodsTypeAndDate(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("goodsType") String goodsType,
            @Param("date") String date,
            Pageable pageable);

            @Query("""
                SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                FROM Order o
                WHERE o.deleted = false
                  AND o.userId = :userId
                  AND (:search IS NULL OR o.goodsType LIKE %:search% OR o.status LIKE %:search%)
                  AND (:goodsType IS NULL OR o.goodsType = :goodsType)
            """)
            Page<OrderDTO> findByUserIdAndSearchAndGoodsType(
                    @Param("userId") Long userId,
                    @Param("search") String search,
                    @Param("goodsType") String goodsType,
                    Pageable pageable);
        
            @Query("""
                SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                FROM Order o
                WHERE o.deleted = false
                  AND o.userId = :userId
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
                  AND o.userId = :userId
                  AND (:goodsType IS NULL OR o.goodsType = :goodsType)
                  AND (:date IS NULL OR FUNCTION('DATE', o.createdAt) = :date)
            """)
            Page<OrderDTO> findByUserIdAndGoodsTypeAndDate(
                    @Param("userId") Long userId,
                    @Param("goodsType") String goodsType,
                    @Param("date") String date,
                    Pageable pageable);
        
            @Query("""
                SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                FROM Order o
                WHERE o.deleted = false
                  AND o.userId = :userId
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
                  AND o.userId = :userId
                  AND (:goodsType IS NULL OR o.goodsType = :goodsType)
            """)
            Page<OrderDTO> findByUserIdAndGoodsType(
                    @Param("userId") Long userId,
                    @Param("goodsType") String goodsType,
                    Pageable pageable);
        
            @Query("""
                SELECT new vn.aptech.petspa.dto.OrderDTO(o)
                FROM Order o
                WHERE o.deleted = false
                  AND o.userId = :userId
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
                  AND o.userId = :userId
            """)
            Page<OrderDTO> findByUserId(
                    @Param("userId") Long userId,
                    Pageable pageable);
    

}
