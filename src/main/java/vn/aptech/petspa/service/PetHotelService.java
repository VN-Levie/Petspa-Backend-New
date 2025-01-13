package vn.aptech.petspa.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.aptech.petspa.dto.*;
import vn.aptech.petspa.entity.*;

import vn.aptech.petspa.repository.*;
import vn.aptech.petspa.util.ZDebug;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PetHotelService {

    @Autowired
    private PetHotelRoomRepository petHotelRoomRepository;

    @Autowired
    private PetHotelRoomDetailRepository petHotelRoomDetailRepository;

    public List<PetHotelRoom> findAvailableRooms(Long petHotelId, LocalDateTime checkIn, LocalDateTime checkOut) {
        // Lấy tất cả các phòng trong khách sạn
        List<PetHotelRoom> rooms = petHotelRoomRepository.findByPetHotelId(petHotelId);

        // Loại bỏ các phòng đã được đặt trong khoảng thời gian yêu cầu
        return rooms.stream()
                .filter(room -> petHotelRoomDetailRepository.findByPetHotelRoomId(room.getId()).stream()
                        .noneMatch(detail -> !(detail.getCheckOutTime().isBefore(checkIn) ||
                                detail.getCheckInTime().isAfter(checkOut))))
                .collect(Collectors.toList());
    }

    // check if room is available
    public boolean isRoomAvailable(Long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        return petHotelRoomDetailRepository.findByPetHotelRoomId(roomId).stream()
                .noneMatch(detail -> !(detail.getCheckOutTime().isBefore(checkIn) ||
                        detail.getCheckInTime().isAfter(checkOut)));
    }

    public boolean isRoomAvailable(Long id, LocalDate date, LocalDate endDate) {
        return petHotelRoomDetailRepository.findByPetHotelRoomId(id).stream()
                .noneMatch(detail -> !(detail.getCheckOutTime().toLocalDate().isBefore(date) ||
                        detail.getCheckInTime().toLocalDate().isAfter(endDate)));
    }
}
