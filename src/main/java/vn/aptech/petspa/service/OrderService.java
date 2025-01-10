package vn.aptech.petspa.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.aptech.petspa.dto.AddressBookDTO;
import vn.aptech.petspa.dto.OrderDTO;
import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.dto.SpaProductDTO;
import vn.aptech.petspa.entity.AddressBook;
import vn.aptech.petspa.entity.SpaCategory;
import vn.aptech.petspa.entity.SpaProduct;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.exception.NotFoundException;
import vn.aptech.petspa.repository.AddressBookRepository;
import vn.aptech.petspa.repository.OrderRepository;
import vn.aptech.petspa.repository.PetHealthRepository;
import vn.aptech.petspa.repository.PetPhotoRepository;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import vn.aptech.petspa.repository.SpaCategoryRepository;
import vn.aptech.petspa.repository.SpaProductRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.util.JwtUtil;

@Service
public class OrderService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetTypeRepository petTypeRepository;

    @Autowired
    private PetHealthRepository petHealthRepository;

    @Autowired
    private PetPhotoRepository petPhotoRepository;

    @Autowired
    private SpaCategoryRepository spaCategoryRepository;

    @Autowired
    private SpaProductRepository spaProductRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FileService fileService;

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrder(Long userId, String search, String goodsType, String date, Pageable pageable) {
        if(search != null && goodsType != null && date != null) {
            return orderRepository.findByUserIdAndSearchAndGoodsTypeAndDate(userId, search, goodsType, date, pageable);
        } else if(search != null && goodsType != null) {
            return orderRepository.findByUserIdAndSearchAndGoodsType(userId, search, goodsType, pageable);
        } else if(search != null && date != null) {
            return orderRepository.findByUserIdAndSearchAndDate(userId, search, date, pageable);
        } else if(goodsType != null && date != null) {
            return orderRepository.findByUserIdAndGoodsTypeAndDate(userId, goodsType, date, pageable);
        } else if(search != null) {
            return orderRepository.findByUserIdAndSearch(userId, search, pageable);
        } else if(goodsType != null) {
            return orderRepository.findByUserIdAndGoodsType(userId, goodsType, pageable);
        } else if(date != null) {
            return orderRepository.findByUserIdAndDate(userId, date, pageable);
        } else {
            return orderRepository.findByUserId(userId, pageable);
        }

    }
}
