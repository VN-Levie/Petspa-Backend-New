package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.aptech.petspa.entity.PetHotelRoomDetail;
import vn.aptech.petspa.util.OrderStatusType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PetHotelRoomDetailRepository extends JpaRepository<PetHotelRoomDetail, Long> {
    // Tìm chi tiết phòng theo phòng cụ thể
    List<PetHotelRoomDetail> findByPetHotelRoom_Id(Long petHotelRoomId);

    // Tìm tất cả các booking theo thời gian check-in và check-out
    List<PetHotelRoomDetail> findByCheckInTimeBetweenAndCheckOutTimeBetween(
            LocalDateTime checkInStart, LocalDateTime checkInEnd,
            LocalDateTime checkOutStart, LocalDateTime checkOutEnd);

    // Tìm tất cả booking đang hoạt động
    List<PetHotelRoomDetail> findByStatus(OrderStatusType status);
}
