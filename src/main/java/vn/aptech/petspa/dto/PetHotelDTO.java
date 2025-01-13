package vn.aptech.petspa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelDTO {
    private Long id; // ID khách sạn
    private List<PetHotelRoomDTO> rooms; // Danh sách phòng

    
}
