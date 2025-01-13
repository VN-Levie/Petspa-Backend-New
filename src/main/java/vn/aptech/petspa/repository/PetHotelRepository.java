package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.aptech.petspa.entity.PetHotel;

@Repository
public interface PetHotelRepository extends JpaRepository<PetHotel, Long> {
    // Các phương thức truy vấn tùy chỉnh (nếu cần) có thể thêm vào đây
}
