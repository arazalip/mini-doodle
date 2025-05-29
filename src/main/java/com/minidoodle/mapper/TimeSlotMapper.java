package com.minidoodle.mapper;

import com.minidoodle.dto.TimeSlotDTO;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.User;
import com.minidoodle.model.Meeting;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TimeSlotMapper {
    public TimeSlotDTO toDTO(TimeSlot timeSlot) {
        if (timeSlot == null) return null;
        
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setId(timeSlot.getId());
        dto.setUserId(timeSlot.getCalendar().getUser().getId());
        dto.setStartTime(timeSlot.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        dto.setEndTime(timeSlot.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        dto.setStatus(timeSlot.getStatus());
        return dto;
    }

    public TimeSlot toEntity(TimeSlotDTO dto, User user) {
        if (dto == null) return null;
        
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(dto.getId());
        timeSlot.setCalendar(user.getCalendar());
        timeSlot.setStartTime(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
        timeSlot.setEndTime(dto.getEndTime().atZone(ZoneId.systemDefault()).toInstant());
        timeSlot.setStatus(dto.getStatus());
        return timeSlot;
    }

    public void updateEntity(TimeSlot timeSlot, TimeSlotDTO dto, Meeting meeting) {
        if (timeSlot == null || dto == null) return;

        timeSlot.setStartTime(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
        timeSlot.setEndTime(dto.getEndTime().atZone(ZoneId.systemDefault()).toInstant());
        timeSlot.setStatus(dto.getStatus());

        if (meeting != null) {
            timeSlot.setMeeting(meeting);
        }
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
} 