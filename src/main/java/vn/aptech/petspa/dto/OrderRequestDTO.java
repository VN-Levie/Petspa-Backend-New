package vn.aptech.petspa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.aptech.petspa.entity.Order;
import vn.aptech.petspa.util.GoodsType;
import vn.aptech.petspa.util.PaymentType;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
public class OrderRequestDTO {

    private Long id;

    private Long userId;

    private Long petId = 0L;

    private String address;

    private Double latitude;

    private Double longitude;

    private PaymentType paymentMethod; // Enum nếu có thể: COD, VNPAY, ...

    private List<CartItemDTO> cart;

    private Double shippingFee;

    private Double total;

    private Double subtotal;

    private String name;

    private String phone;

    private GoodsType goodsType; // spa, shop, hotel

    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7") // Định dạng ngày
    private Date date;

    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7") // Định dạng ngày
    private Date endDate; // Ngày check-out hotel

    @Nullable
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+7") // Định dạng giờ
    private Date startTime;

    @Nullable
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+7") // Định dạng giờ
    private Date endTime; // Giờ kết thúc

    public Order toEntity() {
        Order order = new Order();
        order.setId(this.getId());
        if (this.getId() == null || this.getId() <= 0) {
            this.setId(null);
        }
        order.setFreeformAddress(this.getAddress());
        order.setTotalPrice(this.getTotal());
        order.setName(this.getName());
        order.setPhone(this.getPhone());
        order.setGoodsType(this.getGoodsType());

        return order;
    }

}
