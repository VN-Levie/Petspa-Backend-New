package vn.aptech.petspa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class HotelService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetHotelRepository petHotelRepository;

    @Autowired
    private PetHotelRoomTypeRepository petHotelRoomTypeRepository;

    @Autowired
    private PetHotelRoomRepository petHotelRoomRepository;

    @Autowired
    private PetHotelRoomDetailRepository petHotelRoomDetailRepository;

    public void addRoom(PetHotelRoomDTO categoryDTO) {
        PetHotel petHotel = petHotelRepository.findById(1L).orElse(null);
        if (petHotel == null) {
            throw new RuntimeException("Hotel not found");
        }

        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(categoryDTO.getRoomTypeId()).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }

        PetHotelRoom room = new PetHotelRoom();
        room.setPetHotel(petHotel);
        room.setRoomType(roomType);
        room.setPrice(categoryDTO.getPrice());
        Set<PetHotelRoomDetail> roomDetails = new HashSet<>();
        room.setRoomDetails(roomDetails);

        petHotelRoomRepository.save(room);

    }

}
