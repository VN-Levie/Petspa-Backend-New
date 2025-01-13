package vn.aptech.petspa.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import vn.aptech.petspa.dto.*;
import vn.aptech.petspa.entity.*;
import vn.aptech.petspa.exception.NotFoundException;
import vn.aptech.petspa.repository.*;
import vn.aptech.petspa.util.*;

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
    private PetHotelRoomRepository petHotelRoomRepository;

    @Autowired
    private PetHotelRoomDetailRepository petHotelRoomDetailRepository;

    @Autowired
    private PetHotelService petHotelService;

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
        List<OrderProduct> orderProducts = new ArrayList<>();
        if (orderDTO.getGoodsType() == GoodsType.SHOP) {
            for (CartItemDTO cI : orderDTO.getCart()) {
                ShopProduct shopProduct = shopProductRepository.findById(cI.getId())
                        .orElseThrow(() -> new NotFoundException("Shop product not found"));
                if (shopProduct.getQuantity() < cI.getQuantity()) {
                    throw new IllegalArgumentException("Not enough quantity for product " + shopProduct.getName());
                }
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrder(order);
                orderProduct.setGoodsType(GoodsType.SHOP);
                orderProduct.setId(shopProduct.getId());
                orderProduct.setQuantity(cI.getQuantity());
                orderProduct.setPrice(shopProduct.getPrice());
                orderProducts.add(orderProduct);
            }
        }

        if (orderDTO.getGoodsType() == GoodsType.HOTEL) {
            processHotelBooking(orderDTO, user, order, orderProducts);
        }

        // Kiểm tra số slot trong SPA
        if (orderDTO.getGoodsType() == GoodsType.SPA) {
            processSpaBooking(orderDTO, user, order, orderProducts);
        }

        order.setStatus(OrderStatusType.PENDING);
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

    private void processSpaBooking(OrderRequestDTO orderDTO, User user, Order order, List<OrderProduct> orderProducts) {
        if (orderDTO.getDate() == null || orderDTO.getStartTime() == null || orderDTO.getEndTime() == null) {
            throw new IllegalArgumentException("Date, start time and end time are required for spa orders");
        }
        if (orderDTO.getPetId() == null || orderDTO.getPetId() <= 0) {
            throw new IllegalArgumentException("Pet is required for spa orders");
        }

        try {
            appSettingsService.validateWorkingConditions(orderDTO.getDate(), orderDTO.getStartTime());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        Pet pet = petRepository.findByIdAndUser(orderDTO.getPetId(), user.getId())
                .orElseThrow(() -> new NotFoundException("Pet not found"));
        order.setPet(pet);

        // Lấy tất cả Order cảu pet này mà chưa hoàn thành | pet,
        // Lấy danh sách sản phẩm trong giỏ hàng
        Set<Long> cartProductIds = orderDTO.getCart().stream()
                .map(CartItemDTO::getId)
                .collect(Collectors.toSet());

        // Tìm các đơn hàng xung đột trong khoảng thời gian 3 ngày
        List<Order> conflictingOrders = orderRepository.findConflictingOrders(
                pet.getId(),
                OrderStatusType.PENDING,
                GoodsType.SPA,
                orderDTO.getDate().minusDays(3),
                orderDTO.getDate().plusDays(3), cartProductIds);

        // Kiểm tra từng sản phẩm trùng lặp
        for (Order o : conflictingOrders) {
            for (OrderProduct op : o.getOrderProducts()) {
                if (cartProductIds.contains(op.getId())) {
                    SpaProduct spaProduct = spaProductRepository.findById(op.getId())
                            .orElseThrow(() -> new NotFoundException("Spa product not found"));
                    throw new IllegalArgumentException(
                            "You have already booked " + pet.getName() + " for "
                                    + spaProduct.getName() + " within 3 days.");
                }
            }
        }
        int totalSlotRequired = 0;
        for (CartItemDTO cI : orderDTO.getCart()) {
            SpaProduct spaProduct = spaProductRepository.findById(cI.getId())
                    .orElseThrow(() -> new NotFoundException("Spa product not found"));
            totalSlotRequired += spaProduct.getSlotRequired();

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setGoodsType(GoodsType.SPA);
            orderProduct.setId(spaProduct.getId());
            orderProduct.setQuantity(cI.getQuantity());
            orderProduct.setPrice(spaProduct.getPrice());
            orderProducts.add(orderProduct);

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

    private void processHotelBooking(OrderRequestDTO orderDTO, User user, Order order,
            List<OrderProduct> orderProducts) {
        for (CartItemDTO cI : orderDTO.getCart()) {
            PetHotelRoom room = petHotelRoomRepository.findById(cI.getId())
                    .orElseThrow(() -> new NotFoundException("Hotel room not found"));
            if (!petHotelService.isRoomAvailable(cI.getId(), orderDTO.getDate(), orderDTO.getEndDate())) {
                throw new IllegalArgumentException(
                        "Room " + room.getName() + " is not available for the selected date");
            }
            Pet pet = petRepository.findByIdAndUser(orderDTO.getPetId(), user.getId())
                    .orElseThrow(() -> new NotFoundException("Pet not found"));
            order.setPet(pet);
            PetHotelRoomDetail roomDetail = new PetHotelRoomDetail();
            roomDetail.setRoom(room);
            roomDetail.setOrder(order);
            roomDetail.setPet(pet);
            LocalDateTime checkInTime = LocalDateTime.of(orderDTO.getDate(), LocalTime.of(6, 0));
            roomDetail.setCheckInTime(checkInTime);
            LocalDateTime checkOutTime = LocalDateTime.of(orderDTO.getEndDate(), LocalTime.of(12, 0));
            roomDetail.setCheckOutTime(checkOutTime);
            roomDetail.setStatus(OrderStatusType.PENDING);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setGoodsType(GoodsType.HOTEL);
            orderProduct.setId(room.getId());
            orderProduct.setQuantity(cI.getQuantity());
            orderProduct.setPrice(room.getPrice());
            orderProducts.add(orderProduct);
        }
    }

}
