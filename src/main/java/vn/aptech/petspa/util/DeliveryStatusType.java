package vn.aptech.petspa.util;

public enum DeliveryStatusType {
    PENDING, // Đơn hàng đang chờ xử lý
    ON_THE_WAY, // Đang trên đường đến
    PICKED_UP, // Đã pick up
    ARRIVED_AT_SHOP, // Đã đến shop
    WAITING_FOR_PICKUP, // Đang chờ được đón (sau khi dùng xong spa)
    DELIVERED, // Đã giao thành công
    RETURNED, // Hàng bị trả lại
    DONE, // Hoàn thành
    CANCELLED; // Đã hủy
}
