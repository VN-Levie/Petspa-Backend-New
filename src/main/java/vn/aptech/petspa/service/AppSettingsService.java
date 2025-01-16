package vn.aptech.petspa.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.aptech.petspa.dto.RestDayDTO;
import vn.aptech.petspa.entity.AppSettings;
import vn.aptech.petspa.repository.AppSettingsRepository;
import vn.aptech.petspa.util.ZDebug;

@Service
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

    public String getByKey(String key) {
        Optional<AppSettings> setting = appSettingsRepository.findByKey(key);
        return setting.map(AppSettings::getValue).orElse(null);
    }

    public void addSpecificRestDay(String date, String reason) throws JsonProcessingException {
        try {
            // Lấy cấu hình `specificRestDays` từ cơ sở dữ liệu
            Optional<AppSettings> specificRestDaysSetting = appSettingsRepository.findByKey("specificRestDays");

            List<Map<String, String>> specificRestDays;
            if (specificRestDaysSetting.isPresent() && specificRestDaysSetting.get().getValue() != null) {
                try {
                    specificRestDays = objectMapper.readValue(
                            specificRestDaysSetting.get().getValue(),
                            new TypeReference<List<Map<String, String>>>() {
                            });
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Invalid JSON format for specificRestDays");
                }
            } else {
                specificRestDays = new ArrayList<>();
            }

            // Kiểm tra trùng ngày nghỉ
            for (Map<String, String> restDay : specificRestDays) {
                if (restDay.get("date").equals(date)) {
                    throw new IllegalArgumentException("The date " + date + " is already a rest day.");
                }
            }

            // Thêm ngày nghỉ mới
            Map<String, String> newRestDay = new HashMap<>();
            newRestDay.put("date", date);
            newRestDay.put("reason", reason);
            specificRestDays.add(newRestDay);

            // Lưu cấu hình mới
            String updatedValue = objectMapper.writeValueAsString(specificRestDays);
            AppSettings appSettings = specificRestDaysSetting.orElse(new AppSettings());
            appSettings.setKey("specificRestDays");
            appSettings.setValue(updatedValue);
            appSettingsRepository.save(appSettings);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error adding specific rest day: " + e.getMessage());
        }
    }

    public void deleteSpecificRestDay(String date) throws JsonProcessingException {
        Optional<AppSettings> specificRestDaysSetting = appSettingsRepository.findByKey("specificRestDays");

        if (specificRestDaysSetting.isPresent() && specificRestDaysSetting.get().getValue() != null) {
            List<Map<String, String>> specificRestDays = objectMapper.readValue(
                    specificRestDaysSetting.get().getValue(),
                    new TypeReference<List<Map<String, String>>>() {
                    });

            // Xóa ngày nghỉ theo ngày
            boolean removed = specificRestDays.removeIf(restDay -> restDay.get("date").equals(date));
            if (!removed) {
                throw new IllegalArgumentException("Date not found in specific rest days.");
            }

            // Lưu cấu hình mới
            String updatedValue = objectMapper.writeValueAsString(specificRestDays);
            AppSettings appSettings = specificRestDaysSetting.get();
            appSettings.setValue(updatedValue);
            appSettingsRepository.save(appSettings);
        } else {
            throw new IllegalArgumentException("No specific rest days found to delete.");
        }
    }

    public void updateWorkingHours(List<Map<String, Object>> weeklyWorkingHours) throws JsonProcessingException {
        try {
            // Kiểm tra danh sách đầu vào có hợp lệ không
            if (weeklyWorkingHours == null || weeklyWorkingHours.isEmpty()) {
                throw new IllegalArgumentException("Weekly working hours must not be null or empty.");
            }
    
            // Chuyển đổi danh sách thành JSON
            String updatedValue = objectMapper.writeValueAsString(weeklyWorkingHours);
    
            // Lấy cấu hình hiện tại từ cơ sở dữ liệu
            Optional<AppSettings> workingHoursSetting = appSettingsRepository.findByKey("weeklyWorkingHours");
    
            // Cập nhật giá trị mới
            AppSettings appSettings = workingHoursSetting.orElse(new AppSettings());
            appSettings.setKey("weeklyWorkingHours");
            appSettings.setValue(updatedValue);
    
            // Lưu lại vào cơ sở dữ liệu
            appSettingsRepository.save(appSettings);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating weekly working hours: " + e.getMessage(), e);
        }
    }
    

}
