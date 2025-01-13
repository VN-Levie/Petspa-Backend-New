package vn.aptech.petspa.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.aptech.petspa.entity.AppSettings;
import vn.aptech.petspa.repository.AppSettingsRepository;
import vn.aptech.petspa.util.ZDebug;

public class AppSettingsService {
    @Autowired
    private AppSettingsRepository appSettingsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isRestDay(LocalDate date) throws JsonProcessingException {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 2. Kiểm tra ngày nghỉ theo lịch
        Optional<AppSettings> specificRestDaysSetting = appSettingsRepository.findByKey("specificRestDays");
        if (specificRestDaysSetting.isPresent() && specificRestDaysSetting.get().getValue() != null) {
            try {
                List<Map<String, String>> specificRestDays = objectMapper.readValue(
                        specificRestDaysSetting.get().getValue(),
                        new TypeReference<>() {
                        });
                String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                for (Map<String, String> restDay : specificRestDays) {
                    if (dateString.equals(restDay.get("date"))) {
                        return true;
                    }
                }
            } catch (Exception e) {
                ZDebug.gI().logException("Error parsing specificRestDays", e);
            }
        }

        Optional<AppSettings> workingHoursSetting = appSettingsRepository.findByKey("weeklyWorkingHours");
        if (workingHoursSetting.isPresent() && workingHoursSetting.get().getValue() != null) {
            // Chuyển đổi JSON thành danh sách đối tượng
            List<Map<String, String>> weeklyWorkingHours = objectMapper.readValue(
                    workingHoursSetting.get().getValue(),
                    new TypeReference<>() {
                    });

            // Tìm cấu hình cho ngày hiện tại
            for (Map<String, String> workingHour : weeklyWorkingHours) {
                if (dayOfWeek.name().equals(workingHour.get("dayOfWeek"))) {
                    // Kiểm tra nếu là ngày nghỉ hoàn toàn
                    boolean isRestDay = Boolean.parseBoolean(workingHour.getOrDefault("isRestDay", "false"));
                    if (isRestDay) {
                        return true; // Ngày nghỉ hoàn toàn
                    }
                }
            }
        }

        return false;
    }

    // check khung giờ hoạt động
    public boolean isWorkingHour(LocalDate date, LocalTime time) throws JsonProcessingException {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 1. Lấy dữ liệu JSON từ AppSettings
        Optional<AppSettings> workingHoursSetting = appSettingsRepository.findByKey("weeklyWorkingHours");
        if (workingHoursSetting.isPresent() && workingHoursSetting.get().getValue() != null) {
            // Chuyển đổi JSON thành danh sách đối tượng
            List<Map<String, String>> weeklyWorkingHours = objectMapper.readValue(
                    workingHoursSetting.get().getValue(),
                    new TypeReference<>() {
                    });

            // Tìm cấu hình cho ngày hiện tại
            for (Map<String, String> workingHour : weeklyWorkingHours) {
                if (dayOfWeek.name().equals(workingHour.get("dayOfWeek"))) {
                    // Kiểm tra nếu là ngày nghỉ hoàn toàn
                    boolean isRestDay = Boolean.parseBoolean(workingHour.getOrDefault("isRestDay", "false"));
                    if (isRestDay) {
                        return false; // Ngày nghỉ hoàn toàn
                    }

                    // Lấy giờ mở cửa và đóng cửa
                    LocalTime openTime = workingHour.get("openTime") != null
                            ? LocalTime.parse(workingHour.get("openTime"))
                            : LocalTime.MIN;
                    LocalTime closeTime = workingHour.get("closeTime") != null
                            ? LocalTime.parse(workingHour.get("closeTime"))
                            : LocalTime.MAX;

                    // Lấy giờ nghỉ (nếu có)
                    LocalTime restStartTime = workingHour.get("restStartTime") != null
                            ? LocalTime.parse(workingHour.get("restStartTime"))
                            : null;
                    LocalTime restEndTime = workingHour.get("restEndTime") != null
                            ? LocalTime.parse(workingHour.get("restEndTime"))
                            : null;

                    // Kiểm tra giờ hiện tại
                    if (time.isBefore(openTime) || time.isAfter(closeTime)) {
                        return false; // Ngoài giờ mở/đóng cửa
                    }
                    if (restStartTime != null && restEndTime != null
                            && time.isAfter(restStartTime) && time.isBefore(restEndTime)) {
                        return false; // Trong giờ nghỉ
                    }

                    return true; // Trong giờ làm việc
                }
            }
        }

        return false; // Không tìm thấy cấu hình cho ngày hiện tại
    }

    public void validateWorkingConditions(LocalDate date, LocalTime startTime) {
        try {
            if (isRestDay(date)) {
                throw new IllegalArgumentException(
                        "Sorry, we are closed on this day. For more information, please contact us.");
            }

            if (!isWorkingHour(date, startTime)) {
                throw new IllegalArgumentException(
                        "Sorry, we cannot accept orders at this selected time. For more information, please contact us.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error validating working conditions", e);
        }
    }

}
