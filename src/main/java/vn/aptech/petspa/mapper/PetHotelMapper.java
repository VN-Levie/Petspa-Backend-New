package vn.aptech.petspa.mapper;

import vn.aptech.petspa.dto.*;
import vn.aptech.petspa.entity.*;

import java.util.stream.Collectors;

public class PetHotelMapper {

    public static PetHotelDTO toDto(PetHotel petHotel) {
        return new PetHotelDTO(
                petHotel.getId(),
                petHotel.getRooms() != null ? petHotel.getRooms().stream()
                        .map(PetHotelMapper::toDto)
                        .collect(Collectors.toList())
                        : null);
    }

    public static PetHotelRoomDTO toDto(PetHotelRoom petHotelRoom) {
        return new PetHotelRoomDTO(
                petHotelRoom.getId(),
                petHotelRoom.getName(),
                petHotelRoom.getDescription(),
                petHotelRoom.getPrice(),
                toDto(petHotelRoom.getRoomType()),
                petHotelRoom.getRoomDetails() != null ? petHotelRoom.getRoomDetails().stream()
                        .map(PetHotelMapper::toDto)
                        .collect(Collectors.toList())
                        : null,
                petHotelRoom.getDeleted());
    }

    public static PetHotelRoomTypeDTO toDto(PetHotelRoomType roomType) {
        return new PetHotelRoomTypeDTO(
                roomType.getId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getDeleted());
    }

    public static PetHotelRoomDetailDTO toDto(PetHotelRoomDetail roomDetail) {
        return new PetHotelRoomDetailDTO(
                roomDetail.getId(),
                roomDetail.getCheckInTime(),
                roomDetail.getCheckOutTime(),
                roomDetail.getStatus() != null ? roomDetail.getStatus().name() : null,
                roomDetail.getPet() != null ? roomDetail.getPet().getId() : null);
    }
}
