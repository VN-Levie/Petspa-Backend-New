package vn.aptech.petspa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.dto.PetHotelDTO;
import vn.aptech.petspa.dto.PetHotelRoomDTO;
import vn.aptech.petspa.dto.PetHotelRoomDetailDTO;
import vn.aptech.petspa.dto.PetHotelRoomTypeDTO;
import vn.aptech.petspa.dto.SpaServiceScheduleDTO;
import vn.aptech.petspa.util.ScheduleDetailsConverter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pet_hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotel extends BaseEntity {

    @OneToMany(mappedBy = "petHotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PetHotelRoom> rooms;

}
