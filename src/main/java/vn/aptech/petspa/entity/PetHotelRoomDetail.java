package vn.aptech.petspa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.util.OrderStatusType;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pet_hotel_room_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelRoomDetail extends BaseEntity {

    private LocalDateTime checkInTime; // Giờ check in

    private LocalDateTime checkOutTime; // Giờ check out

    private OrderStatusType status; // Trạng thái

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_hotel_room_id", nullable = false)
    private PetHotelRoom room; // Phòng nào
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Đơn hàng nào
}
