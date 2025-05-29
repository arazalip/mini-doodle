package com.minidoodle.helper;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.dto.TimeSlotDTO;
import com.minidoodle.dto.UserDTO;
import com.minidoodle.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;

public class TestDataHelper {

    public static User createTestUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    public static UserDTO createTestUserDTO(Long id, String email, String name) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setEmail(email);
        userDTO.setName(name);
        return userDTO;
    }

    public static Calendar createTestCalendar(Long id) {
        Calendar calendar = new Calendar();
        calendar.setId(id);
        return calendar;
    }

    public static TimeSlot createTestTimeSlot(Long id, Calendar calendar, Instant startTime, Instant endTime, TimeSlotStatus status) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(id);
        timeSlot.setCalendar(calendar);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);
        timeSlot.setStatus(status);
        return timeSlot;
    }

    public static TimeSlotDTO createTestTimeSlotDTO(Long id, Long userId, LocalDateTime startTime, LocalDateTime endTime, TimeSlotStatus status) {
        TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
        timeSlotDTO.setId(id);
        timeSlotDTO.setUserId(userId);
        timeSlotDTO.setStartTime(startTime);
        timeSlotDTO.setEndTime(endTime);
        timeSlotDTO.setStatus(status);
        return timeSlotDTO;
    }

    public static Meeting createTestMeeting(Long id, String title, String description, User organizer, TimeSlot timeSlot, User... participants) {
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setOrganizer(organizer);
        meeting.setTimeSlot(timeSlot);
        meeting.setParticipants(new HashSet<>(Arrays.asList(participants)));
        return meeting;
    }

    public static MeetingDTO createTestMeetingDTO(Long id, String title, String description, Long organizerId, Long timeSlotId, Long... participantIds) {
        MeetingDTO meetingDTO = new MeetingDTO();
        meetingDTO.setId(id);
        meetingDTO.setTitle(title);
        meetingDTO.setDescription(description);
        meetingDTO.setOrganizerId(organizerId);
        meetingDTO.setTimeSlotId(timeSlotId);
        meetingDTO.setParticipantIds(new HashSet<>(Arrays.asList(participantIds)));
        return meetingDTO;
    }

    public static User createDefaultTestUser() {
        return createTestUser(1L, "test@example.com", "Test User");
    }

    public static UserDTO createDefaultTestUserDTO() {
        return createTestUserDTO(1L, "test@example.com", "Test User");
    }

    public static Calendar createDefaultTestCalendar() {
        return createTestCalendar(1L);
    }

    public static TimeSlot createDefaultTestTimeSlot() {
        Instant now = Instant.now();
        return createTestTimeSlot(1L, createDefaultTestCalendar(), now, now.plus(1, ChronoUnit.HOURS), TimeSlotStatus.AVAILABLE);
    }

    public static TimeSlotDTO createDefaultTestTimeSlotDTO() {
        LocalDateTime now = LocalDateTime.now();
        return createTestTimeSlotDTO(1L, 1L, now, now.plusHours(1), TimeSlotStatus.AVAILABLE);
    }

    public static Meeting createDefaultTestMeeting() {
        User organizer = createDefaultTestUser();
        User participant = createTestUser(2L, "participant@example.com", "Test Participant");
        TimeSlot timeSlot = createDefaultTestTimeSlot();
        return createTestMeeting(1L, "Test Meeting", "Test Description", organizer, timeSlot, participant);
    }

    public static MeetingDTO createDefaultTestMeetingDTO() {
        return createTestMeetingDTO(1L, "Test Meeting", "Test Description", 1L, 1L, 2L);
    }
} 