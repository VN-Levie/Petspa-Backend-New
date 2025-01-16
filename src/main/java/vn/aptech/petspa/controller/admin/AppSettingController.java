package vn.aptech.petspa.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import vn.aptech.petspa.dto.*;
import vn.aptech.petspa.entity.*;
import vn.aptech.petspa.repository.*;
import vn.aptech.petspa.service.*;
import vn.aptech.petspa.util.*;

import org.springframework.data.domain.*;

@RestController
@RequestMapping("/api/admin/setting")
public class AppSettingController {
    @Autowired
    private AppSettingsService appSettingService;

    @GetMapping("/get-by-key")
    public ResponseEntity<ApiResponse> getByKey(@RequestParam("key") String key) {
        try {
            // Gọi service để lấy giá trị cài đặt dựa trên key
            String value = appSettingService.getByKey(key);
            if (value == null) {
                return ApiResponse.notFound("Setting not found");
            }
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved setting", value));
        } catch (Exception e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @PostMapping("/specific-rest-day/add")
    public ResponseEntity<ApiResponse> addSpecificRestDay(@RequestParam("restDayDTO") String restDayJson)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestDayDTO categoryDTO = objectMapper.readValue(restDayJson, RestDayDTO.class);
        String date = categoryDTO.getDate().toString();
        String reason = categoryDTO.getReason();

        if (date == null || reason == null) {
            return ApiResponse.badRequest("Date and reason must not be null.");
        }

        try {
            appSettingService.addSpecificRestDay(date, reason);
            return ResponseEntity.ok(new ApiResponse("Specific rest day added successfully", null));
        } catch (Exception e) {
            return ApiResponse.badRequest("Error adding specific rest day: " + e.getMessage());
        }
    }

    @PostMapping("/specific-rest-day/delete")
    public ResponseEntity<ApiResponse> deleteSpecificRestDay(@RequestParam("restDayDTO") String restDayJson)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        RestDayDTO categoryDTO = objectMapper.readValue(restDayJson, RestDayDTO.class);
        String date = categoryDTO.getDate().toString();
        String reason = categoryDTO.getReason();

        if (date == null) {
            return ApiResponse.badRequest("Date must not be null.");
        }

        try {
            appSettingService.deleteSpecificRestDay(date);
            return ResponseEntity.ok(new ApiResponse("Specific rest day deleted successfully", null));
        } catch (Exception e) {
            return ApiResponse.badRequest("Error deleting specific rest day: " + e.getMessage());
        }
    }

    @PostMapping("/working-hours/update")
    public ResponseEntity<ApiResponse> updateWorkingHours(@RequestBody List<Map<String, Object>> weeklyWorkingHours) {
        try {
            // Gọi service để cập nhật lịch làm việc
            appSettingService.updateWorkingHours(weeklyWorkingHours);

            // Trả về phản hồi thành công
            return ResponseEntity.ok(new ApiResponse("Weekly working hours updated successfully", null));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.badRequest("Error updating weekly working hours: " + e.getMessage());
        }
    }

}
