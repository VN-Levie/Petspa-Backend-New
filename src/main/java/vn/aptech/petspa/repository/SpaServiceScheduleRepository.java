package vn.aptech.petspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.aptech.petspa.entity.SpaServiceSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SpaServiceScheduleRepository extends JpaRepository<SpaServiceSchedule, Long> {

    // Tìm tất cả lịch theo ngày
    List<SpaServiceSchedule> findByDate(LocalDate date);

    // Tìm lịch theo ngày và khoảng thời gian cụ thể
    @Query(value = "SELECT * FROM spa_service_schedules s " +
            "WHERE s.date = :date " +
            "AND JSON_EXTRACT(s.schedule_details, '$.startTime') <= :startTime " +
            "AND JSON_EXTRACT(s.schedule_details, '$.endTime') >= :endTime", nativeQuery = true)
    SpaServiceSchedule findByDateAndTime(LocalDate date, String startTime, String endTime);

}
