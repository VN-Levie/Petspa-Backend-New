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
import vn.aptech.petspa.mapper.PetHotelMapper;
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

        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(categoryDTO.getRoomType().getId()).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }
        // check if room is already exist
        PetHotelRoom roomExist = petHotelRoomRepository.findByName(categoryDTO.getName());
        if (roomExist != null) {
            throw new RuntimeException("Room is already exist");
        }

        PetHotelRoom room = new PetHotelRoom();
        room.setName(categoryDTO.getName());
        room.setPetHotel(petHotel);
        room.setRoomType(roomType);
        room.setPrice(categoryDTO.getPrice());
        Set<PetHotelRoomDetail> roomDetails = new HashSet<>();
        room.setRoomDetails(roomDetails);
        room.setDescription(categoryDTO.getDescription());

        petHotelRoomRepository.save(room);

    }

    public void editRoom(PetHotelRoomDTO categoryDTO) {
        PetHotelRoom room = petHotelRoomRepository.findById(categoryDTO.getId()).orElse(null);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(categoryDTO.getRoomType().getId()).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }

        // check if room is already exist
        PetHotelRoom roomExist = petHotelRoomRepository.findByName(categoryDTO.getName());
        if (roomExist != null && roomExist.getId() != categoryDTO.getId()) {
            throw new RuntimeException("Room is already exist");
        }

        room.setName(categoryDTO.getName());
        room.setRoomType(roomType);
        room.setPrice(categoryDTO.getPrice());
        room.setDescription(categoryDTO.getDescription());

        petHotelRoomRepository.save(room);

    }

    public int deleteRoom(Long id) {
        PetHotelRoom room = petHotelRoomRepository.findById(id).orElse(null);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        room.setDeleted(!room.getDeleted());
        petHotelRoomRepository.save(room);
        return room.getDeleted() ? 0 : 1;
    }

    public PetHotelRoomDTO getRoom(Long id) {
        PetHotelRoom room = petHotelRoomRepository.findById(id).orElse(null);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        return PetHotelMapper.toDto(room);
    }

    @Transactional
    public List<PetHotelRoomDTO> getAllRoom() {
        List<PetHotelRoom> rooms = petHotelRoomRepository.findAll();
        return rooms.stream().map(PetHotelMapper::toDto).collect(Collectors.toList());
    }

    public List<PetHotelRoomTypeDTO> getAllRoomType() {
        List<PetHotelRoomType> roomTypes = petHotelRoomTypeRepository.findAll();
        return roomTypes.stream().map(PetHotelMapper::toDto).collect(Collectors.toList());
    }

    public void addRoomType(PetHotelRoomTypeDTO roomTypeDTO) {
        // check if room type is already exist
        PetHotelRoomType roomTypeExist = petHotelRoomTypeRepository.findByName(roomTypeDTO.getName());
        if (roomTypeExist != null) {
            throw new RuntimeException("Room type is already exist");
        }

        PetHotelRoomType roomType = new PetHotelRoomType();
        roomType.setName(roomTypeDTO.getName());
        roomType.setDescription(roomTypeDTO.getDescription());
        petHotelRoomTypeRepository.save(roomType);
    }

    public void editRoomType(PetHotelRoomTypeDTO roomTypeDTO) {
        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(roomTypeDTO
                .getId()).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }

        // check if room type is already exist
        PetHotelRoomType roomTypeExist = petHotelRoomTypeRepository.findByName(roomTypeDTO.getName());
        if (roomTypeExist != null && roomTypeExist.getId() != roomTypeDTO.getId()) {
            throw new RuntimeException("Room type is already exist");
        }

        roomType.setName(roomTypeDTO.getName());
        roomType.setDescription(roomTypeDTO.getDescription());
        petHotelRoomTypeRepository.save(roomType);

    }

    public int deleteRoomType(Long id) {
        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(id).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }

        roomType.setDeleted(!roomType.getDeleted());
        petHotelRoomTypeRepository.save(roomType);
        return roomType.getDeleted() ? 0 : 1;
    }

    @Transactional(readOnly = true)
    public PetHotelRoomTypeDTO getRoomType(Long id) {
        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(id).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }

        return PetHotelMapper.toDto(roomType);
    }

    @Transactional(readOnly = true)
    public List<PetHotelRoomDTO> getRoomByRoomType(Long roomTypeId) {
        PetHotelRoomType roomType = petHotelRoomTypeRepository.findById(roomTypeId).orElse(null);
        if (roomType == null) {
            throw new RuntimeException("Room type not found");
        }

        List<PetHotelRoom> rooms = petHotelRoomRepository.findByRoomType(roomType);
        return rooms.stream().map(PetHotelMapper::toDto).collect(Collectors.toList());
    }

}
