package vn.aptech.petspa.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.aptech.petspa.entity.SpaScheduleDetail;

@Converter
public class ScheduleDetailsConverter implements AttributeConverter<SpaScheduleDetail, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SpaScheduleDetail scheduleDetails) {
        try {
            return objectMapper.writeValueAsString(scheduleDetails);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert ScheduleDetails to JSON", e);
        }
    }

    @Override
    public SpaScheduleDetail convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, SpaScheduleDetail.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to ScheduleDetails", e);
        }
    }
}
