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

import lombok.NonNull;
import vn.aptech.petspa.dto.AddressBookDTO;
import vn.aptech.petspa.dto.CartItemDTO;
import vn.aptech.petspa.dto.OrderDTO;
import vn.aptech.petspa.dto.OrderRequestDTO;
import vn.aptech.petspa.dto.SpaCategoriesDTO;
import vn.aptech.petspa.dto.SpaProductDTO;
import vn.aptech.petspa.entity.AddressBook;
import vn.aptech.petspa.entity.DeliveryStatus;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.entity.PaymentStatus;
import vn.aptech.petspa.entity.Pet;
import vn.aptech.petspa.entity.ShopProduct;
import vn.aptech.petspa.entity.SpaCategory;
import vn.aptech.petspa.entity.SpaProduct;
import vn.aptech.petspa.entity.SpaServiceSchedule;
import vn.aptech.petspa.entity.User;
import vn.aptech.petspa.exception.NotFoundException;
import vn.aptech.petspa.repository.AddressBookRepository;
import vn.aptech.petspa.repository.DeliveryStatusRepository;
import vn.aptech.petspa.repository.OrderRepository;
import vn.aptech.petspa.repository.PaymentStatusRepository;
import vn.aptech.petspa.repository.PetHealthRepository;
import vn.aptech.petspa.repository.PetPhotoRepository;
import vn.aptech.petspa.repository.PetRepository;
import vn.aptech.petspa.repository.PetTypeRepository;
import vn.aptech.petspa.repository.ShopProductRepository;
import vn.aptech.petspa.repository.SpaCategoryRepository;
import vn.aptech.petspa.repository.SpaProductRepository;
import vn.aptech.petspa.repository.SpaServiceScheduleRepository;
import vn.aptech.petspa.repository.UserRepository;
import vn.aptech.petspa.util.DeliveryStatusType;
import vn.aptech.petspa.util.GoodsType;
import vn.aptech.petspa.util.JwtUtil;
import vn.aptech.petspa.util.PaymentStatusType;

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
    private ShopProductRepository shopProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    @Autowired
    private SpaServiceScheduleRepository spaServiceScheduleRepository;

    @Autowired
    private FileService fileService;

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrder(Long userId, String search, String goodsType, String date, Pageable pageable) {
        if (search != null && goodsType != null && date != null) {
            return orderRepository.findByUserIdAndSearchAndGoodsTypeAndDate(userId, search, goodsType, date, pageable);
        } else if (search != null && goodsType != null) {
            return orderRepository.findByUserIdAndSearchAndGoodsType(userId, search, goodsType, pageable);
        } else if (search != null && date != null) {
            return orderRepository.findByUserIdAndSearchAndDate(userId, search, date, pageable);
        } else if (goodsType != null && date != null) {
            return orderRepository.findByUserIdAndGoodsTypeAndDate(userId, goodsType, date, pageable);
        } else if (search != null) {
            return orderRepository.findByUserIdAndSearch(userId, search, pageable);
        } else if (goodsType != null) {
            return orderRepository.findByUserIdAndGoodsType(userId, goodsType, pageable);
        } else if (date != null) {
            return orderRepository.findByUserIdAndDate(userId, date, pageable);
        } else {
            return orderRepository.findByUserId(userId, pageable);
        }

    }

    @Transactional
    public Order createOrder(OrderRequestDTO orderDTO) {

        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = orderDTO.toEntity();
        order.setUser(user);

        if (orderDTO.getGoodsType() == GoodsType.SHOP) {
            for (CartItemDTO cI : orderDTO.getCart()) {
                ShopProduct shopProduct = shopProductRepository.findById(cI.getId())
                        .orElseThrow(() -> new NotFoundException("Shop product not found"));
                if (shopProduct.getQuantity() < cI.getQuantity()) {
                    throw new IllegalArgumentException("Not enough quantity for product " + shopProduct.getName());
                }

            }
        }

        // Kiểm tra số slot trong SPA
        if (orderDTO.getGoodsType() == GoodsType.SPA) {
            if (orderDTO.getDate() == null || orderDTO.getStartTime() == null || orderDTO.getEndTime() == null) {
                throw new IllegalArgumentException("Date, start time and end time are required for spa orders");
            }
            if (orderDTO.getPetId() == null || orderDTO.getPetId() <= 0) {
                throw new IllegalArgumentException("Pet is required for spa orders");
            }

            Pet pet = petRepository.findByIdAndUser(orderDTO.getPetId(), user.getId())
                    .orElseThrow(() -> new NotFoundException("Pet not found"));
            order.setPet(pet);
            
            for (CartItemDTO cI : orderDTO.getCart()) {
                SpaProduct spaProduct = spaProductRepository.findById(cI.getId())
                        .orElseThrow(() -> new NotFoundException("Spa product not found"));

                // Tìm lịch spa theo ngày và khung giờ
                // Tìm lịch phù hợp
                SpaServiceSchedule schedule = spaServiceScheduleRepository.findByDateAndTime(orderDTO.getDate(),
                        orderDTO.getStartTime(), orderDTO.getEndTime());
                if (schedule == null) {
                    throw new NotFoundException("No schedule found for the selected date and time");
                }

                // Kiểm tra số slot khả dụng
                if (schedule.getBookedSlot() + spaProduct.getSlotRequired() > schedule.getMaxSlot()) {
                    throw new IllegalArgumentException("Not enough slots available for the selected time");
                }

                // Cập nhật số slot đã đặt
                schedule.setBookedSlot(schedule.getBookedSlot() + spaProduct.getSlotRequired());
                spaServiceScheduleRepository.save(schedule);

                // Cập nhật số slot đã đặt
                schedule.setBookedSlot(schedule.getBookedSlot() + spaProduct.getSlotRequired());
                spaServiceScheduleRepository.save(schedule);

                order.setDate(orderDTO.getDate());
                order.setStartTime(orderDTO.getStartTime());
                order.setEndTime(orderDTO.getEndTime());
            }
        }

        order.setStatus("PENDING");
        orderRepository.save(order);

        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setOrder(order);
        paymentStatus.setStatus(PaymentStatusType.PENDING);
        paymentStatus.setPaymentType(orderDTO.getPaymentMethod());
        paymentStatusRepository.save(paymentStatus);

        DeliveryStatus deliveryStatus = new DeliveryStatus();
        deliveryStatus.setOrder(order);
        deliveryStatus.setStatus(DeliveryStatusType.PENDING);
        deliveryStatusRepository.save(deliveryStatus);
        return order;
    }
}
