package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.dto.SpaServiceScheduleDTO;
import vn.aptech.petspa.util.ScheduleDetailsConverter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pet_hotel_room_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelRoomType extends BaseEntity {
    private String name;
    private String description;
}
