package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelRoomDTO {
    private Long id; // ID phòng
    private String name; // Tên phòng
    private String description; // Mô tả phòng
    private Double price; // Giá phòng
    private PetHotelRoomTypeDTO roomType; // Loại phòng
    private List<PetHotelRoomDetailDTO> bookingDetails; // Chi tiết đặt phòng
    private Boolean deleted; // Đánh dấu xóa
}
