package com.minidoodle.mapper;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.model.Meeting;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
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
                .map(User::getId)
                .collect(Collectors.toSet()));
        return dto;
    }

    public Meeting toEntity(MeetingDTO dto, User organizer, TimeSlot timeSlot, Set<User> participants) {
        if (dto == null) return null;
        
        Meeting meeting = new Meeting();
        meeting.setId(dto.getId());
        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());
        meeting.setOrganizer(organizer);
        meeting.setTimeSlot(timeSlot);
        meeting.setParticipants(participants);
        return meeting;
    }

    public void updateEntity(Meeting meeting, MeetingDTO dto, TimeSlot timeSlot) {
        if (meeting == null || dto == null) return;

        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());

        if (timeSlot != null) {
            meeting.setTimeSlot(timeSlot);
        }
    }
} 