package vn.aptech.petspa.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
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
    private AppSettingsService appSettingsService;

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

            try {
                if (appSettingsService.isRestDay(orderDTO.getDate())) {
                    throw new IllegalArgumentException(
                            "Sorry, we are closed on this day.\nFor more information, please contact us.");

                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to check rest day: " + e.getMessage());
            }
            try {
                if (appSettingsService.isWorkingHour(orderDTO.getDate(), orderDTO.getStartTime())) {
                    throw new IllegalArgumentException(
                            "Sorry, we cannot accept orders at this selected time.\nFor more information, please contact us.");

                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to check rest day: " + e.getMessage());
            }

            Pet pet = petRepository.findByIdAndUser(orderDTO.getPetId(), user.getId())
                    .orElseThrow(() -> new NotFoundException("Pet not found"));
            order.setPet(pet);
            int totalSlotRequired = 0;
            for (CartItemDTO cI : orderDTO.getCart()) {
                SpaProduct spaProduct = spaProductRepository.findById(cI.getId())
                        .orElseThrow(() -> new NotFoundException("Spa product not found"));
                totalSlotRequired += spaProduct.getSlotRequired();

            }
            // Tìm lịch spa theo ngày và khung giờ hiện tại
            SpaServiceSchedule schedule = spaServiceScheduleRepository.findByDateAndTime(
                    orderDTO.getDate(), orderDTO.getStartTime(), orderDTO.getEndTime());

            if (schedule == null) {
                throw new NotFoundException("No schedule found for the selected date and time");
            }

            // Kiểm tra số slot khả dụng trong khung giờ hiện tại
            int availableSlots = schedule.getScheduleDetails().getMaxSlot()
                    - schedule.getScheduleDetails().getBookedSlot();
            order.setStartTime(orderDTO.getStartTime());
            if (availableSlots < totalSlotRequired) {
                // Tìm khung giờ tiếp theo (giả sử là khung giờ liền kề)
                LocalTime nextStartTime = orderDTO.getEndTime();
                if (nextStartTime == null) {
                    throw new IllegalArgumentException("Next start time cannot be null");
                }

                LocalTime nextEndTime = nextStartTime
                        .plusMinutes(Duration.between(orderDTO.getStartTime(), orderDTO.getEndTime()).toMinutes());

                SpaServiceSchedule nextSchedule = spaServiceScheduleRepository.findByDateAndTime(
                        orderDTO.getDate(), nextStartTime, nextEndTime);

                if (nextSchedule == null) {
                    throw new IllegalArgumentException(
                            "Sorry, no available slots for the selected and adjacent time slots");
                }

                // Kiểm tra tổng số slot khả dụng trong cả hai khung giờ
                int nextAvailableSlots = nextSchedule.getScheduleDetails().getMaxSlot()
                        - nextSchedule.getScheduleDetails().getBookedSlot();
                if (availableSlots + nextAvailableSlots < totalSlotRequired) {
                    throw new IllegalArgumentException(
                            "Sorry, we are fully booked for the selected and adjacent time slots");
                }

                // Nếu đủ slot, phân bổ vào hai khung giờ
                schedule.getScheduleDetails().setBookedSlot(
                        schedule.getScheduleDetails().getBookedSlot() + Math.min(totalSlotRequired, availableSlots));
                nextSchedule.getScheduleDetails().setBookedSlot(
                        nextSchedule.getScheduleDetails().getBookedSlot() + (totalSlotRequired - availableSlots));
                spaServiceScheduleRepository.save(schedule);
                spaServiceScheduleRepository.save(nextSchedule);
                order.setEndTime(nextEndTime);
            } else {
                // Nếu đủ slot trong khung giờ hiện tại, cập nhật luôn
                schedule.getScheduleDetails()
                        .setBookedSlot(schedule.getScheduleDetails().getBookedSlot() + totalSlotRequired);
                spaServiceScheduleRepository.save(schedule);
                order.setEndTime(orderDTO.getEndTime());
            }

            order.setDate(orderDTO.getDate());

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
