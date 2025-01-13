package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.aptech.petspa.entity.SpaServiceSchedule;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpaServiceScheduleRepository extends JpaRepository<SpaServiceSchedule, Long> {

    // Tìm tất cả lịch theo ngày
    List<SpaServiceSchedule> findByDate(LocalDate date);

    // Tìm lịch theo ngày và khung giờ cụ thể
    @Query("SELECT s FROM SpaServiceSchedule s WHERE s.date = :date AND s.timeSlot = :timeSlot")
    SpaServiceSchedule findByDateAndTimeSlot(LocalDate date, String timeSlot);

    // Lấy tất cả lịch có số slot đã đặt nhỏ hơn tổng slot
    @Query("SELECT s FROM SpaServiceSchedule s WHERE s.date = :date AND s.bookedSlot < s.slot")
    List<SpaServiceSchedule> findAvailableSchedulesByDate(LocalDate date);

    // Kiểm tra xem lịch có còn khả dụng không
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SpaServiceSchedule s WHERE s.date = :date AND s.timeSlot = :timeSlot AND s.bookedSlot < s.slot")
    boolean isSlotAvailable(LocalDate date, String timeSlot);

    // Cập nhật số slot đã đặt cho lịch
    @Query("UPDATE SpaServiceSchedule s SET s.bookedSlot = s.bookedSlot + :bookedSlot WHERE s.id = :scheduleId")
    void updateBookedSlot(Long scheduleId, Integer bookedSlot);
}
