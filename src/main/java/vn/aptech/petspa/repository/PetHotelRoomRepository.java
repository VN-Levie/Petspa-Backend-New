package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.aptech.petspa.entity.PetHotelRoom;
import vn.aptech.petspa.entity.PetHotelRoomType;

import java.util.List;

@Repository
public interface PetHotelRoomRepository extends JpaRepository<PetHotelRoom, Long> {
    // Tìm tất cả các phòng thuộc về một khách sạn cụ thể
    List<PetHotelRoom> findByPetHotelId(Long petHotelId);

    PetHotelRoom findByName(String name);

    List<PetHotelRoom> findByRoomType(PetHotelRoomType roomType);
}
