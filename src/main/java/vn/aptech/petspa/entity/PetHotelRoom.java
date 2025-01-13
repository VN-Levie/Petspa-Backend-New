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
@Table(name = "pet_hotel_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetHotelRoom extends BaseEntity {

    private String name;
    private String description;
    private Double price;
    private PetHotelRoomType roomType;

    @OneToMany(mappedBy = "petHotelRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PetHotelRoomDetail> bookingDetails;
}
