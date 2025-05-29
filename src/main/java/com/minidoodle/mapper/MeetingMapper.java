package com.minidoodle.mapper;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.model.Meeting;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MeetingMapper {
    public MeetingDTO toDTO(Meeting meeting) {
        if (meeting == null) return null;
        
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setOrganizerId(meeting.getOrganizer().getId());
        dto.setTimeSlotId(meeting.getTimeSlot().getId());
        dto.setParticipantIds(meeting.getParticipants().stream()
                .map(participant -> participant.getId())
                .collect(Collectors.toSet()));
        return dto;
    }

    public Meeting toEntity(MeetingDTO dto) {
        if (dto == null) return null;
        
        Meeting meeting = new Meeting();
        meeting.setId(dto.getId());
        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());
        return meeting;
    }
} 