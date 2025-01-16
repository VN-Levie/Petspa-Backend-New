package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelRoomTypeDTO {
    private Long id; // ID loại phòng
    private String name; // Tên loại phòng
    private String description; // Mô tả loại phòng
    private Boolean deleted; // Đánh dấu xóa
}
