package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.aptech.petspa.entity.PetHotelRoomType;

@Repository
public interface PetHotelRoomTypeRepository extends JpaRepository<PetHotelRoomType, Long> {
    // Tìm loại phòng theo tên
    PetHotelRoomType findByName(String name);
}
