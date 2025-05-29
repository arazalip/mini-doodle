package com.minidoodle.service.impl;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.helper.TestDataHelper;
import com.minidoodle.mapper.MeetingMapper;
import com.minidoodle.model.*;
import com.minidoodle.repository.MeetingRepository;
import com.minidoodle.repository.TimeSlotRepository;
import com.minidoodle.repository.UserRepository;
import com.minidoodle.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({MeetingMapper.class})
class MeetingServiceImplTest {

    @MockBean
    private MeetingRepository meetingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TimeSlotRepository timeSlotRepository;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private MeetingServiceImpl meetingService;

    private Meeting meeting;
    private MeetingDTO meetingDTO;
    private User organizer;
    private User participant;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        organizer = TestDataHelper.createDefaultTestUser();
        participant = TestDataHelper.createTestUser(2L, "participant@example.com", "Participant");
        Calendar calendar = new Calendar();
        calendar.setUser(participant);
        participant.setCalendar(calendar);
        timeSlot = TestDataHelper.createDefaultTestTimeSlot();
        calendar.setTimeSlots(new HashSet<>(Collections.singletonList(timeSlot)));
        meeting = TestDataHelper.createTestMeeting(1L, "Test Meeting", "Test Description", organizer, timeSlot, participant);
        meetingDTO = TestDataHelper.createTestMeetingDTO(1L, "Test Meeting", "Test Description", organizer.getId(), timeSlot.getId(), participant.getId());
        timeSlot.setMeeting(meeting);
    }

    @Test
    void createMeeting_ShouldReturnCreatedMeeting() {
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        when(timeSlotRepository.findById(meetingDTO.getTimeSlotId())).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(any(), any(), any(), any())).thenReturn(List.of(timeSlot));
        when(userRepository.findById(meetingDTO.getOrganizerId())).thenReturn(Optional.of(organizer));
        when(userRepository.findAllById(any())).thenReturn(List.of(participant));
        MeetingDTO result = meetingService.createMeeting(meetingDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        assertThat(result.getTitle()).isEqualTo(meetingDTO.getTitle());
        assertThat(result.getOrganizerId()).isEqualTo(meetingDTO.getOrganizerId());

        verify(meetingRepository).save(meeting);
    }

    @Test
    void getMeetingById_WhenMeetingExists_ShouldReturnMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        MeetingDTO result = meetingService.getMeetingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findById(1L);
    }

    @Test
    void getMeetingById_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getMeetingById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).findById(1L);
    }

    @Test
    void getMeetingsByOrganizerId_ShouldReturnMeetings() {
        List<Meeting> meetings = Collections.singletonList(meeting);
        when(meetingRepository.findByOrganizerId(1L)).thenReturn(meetings);

        List<MeetingDTO> result = meetingService.getMeetingsByOrganizerId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findByOrganizerId(1L);
    }

    @Test
    void getMeetingsByParticipantId_ShouldReturnMeetings() {
        List<Meeting> meetings = Collections.singletonList(meeting);
        when(meetingRepository.findByParticipantId(2L)).thenReturn(meetings);

        List<MeetingDTO> result = meetingService.getMeetingsByParticipantId(2L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findByParticipantId(2L);
    }

    @Test
    void addParticipant_WhenMeetingAndUserExist_ShouldAddParticipant() {
        User newParticipant = TestDataHelper.createTestUser(3L, "email", "name");

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newParticipant));
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(eq(3L), any(), any(), any())).thenReturn(List.of(
                meeting.getTimeSlot()
        ));
        MeetingDTO result = meetingService.addParticipant(1L, 3L);

        assertThat(result).isNotNull();
        assertThat(meeting.getParticipants()).contains(newParticipant);
        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(3L);
        verify(meetingRepository).save(meeting);
    }

    @Test
    void addParticipant_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.addParticipant(1L, 3L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).findById(1L);
        verify(userRepository, never()).findById(any());
        verify(meetingRepository, never()).save(any());
    }

    @Test
    void addParticipant_WhenUserDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.addParticipant(1L, 3L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: 3");

        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(3L);
        verify(meetingRepository, never()).save(any());
    }

    @Test
    void removeParticipant_WhenMeetingAndUserExist_ShouldRemoveParticipant() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        participant.getCalendar().getTimeSlots().forEach(ts -> ts.setMeeting(meeting));

        MeetingDTO result = meetingService.removeParticipant(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(meeting.getParticipants()).doesNotContain(participant);
        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(meetingRepository).save(meeting);
    }

    @Test
    void removeParticipant_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.removeParticipant(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).findById(1L);
        verify(userRepository, never()).findById(any());
        verify(meetingRepository, never()).save(any());
    }

    @Test
    void removeParticipant_WhenUserDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.removeParticipant(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: 2");

        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(meetingRepository, never()).save(any());
    }

    @Test
    void updateMeeting_WhenMeetingExists_ShouldReturnUpdatedMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);

        MeetingDTO result = meetingService.updateMeeting(1L, meetingDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).save(meeting);
    }

    @Test
    void updateMeeting_WhenMeetingDoesNotExist_ShouldThrowException() {

        assertThatThrownBy(() -> meetingService.updateMeeting(1L, meetingDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository, never()).save(any());
    }

    @Test
    void deleteMeeting_WhenMeetingExists_ShouldDeleteMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        doNothing().when(meetingRepository).deleteById(1L);

        meetingService.deleteMeeting(1L);

        verify(meetingRepository).deleteById(1L);
    }

    @Test
    void deleteMeeting_WhenMeetingDoesNotExist_ShouldThrowException() {

        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.deleteMeeting(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository, never()).deleteById(any());
    }

    @Test
    void acceptInvitation_ShouldUpdateTimeSlotAndSendNotification() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);
        timeSlot.setMeeting(meeting);

        MeetingDTO result = meetingService.acceptInvitation(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        verify(timeSlotRepository).save(timeSlot);
        verify(notificationService).sendMeetingAcceptance(participant, meeting);
        assertThat(timeSlot.getStatus()).isEqualTo(TimeSlotStatus.BUSY);
    }

    @Test
    void acceptInvitation_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.acceptInvitation(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).findById(1L);
        verify(userRepository, never()).findById(any());
        verify(timeSlotRepository, never()).save(any());
        verify(notificationService, never()).sendMeetingAcceptance(any(), any());
    }

    @Test
    void acceptInvitation_WhenParticipantDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.acceptInvitation(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: 2");

        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(timeSlotRepository, never()).save(any());
        verify(notificationService, never()).sendMeetingAcceptance(any(), any());
    }

    @Test
    void acceptInvitation_WhenUserIsNotParticipant_ShouldThrowException() {
        Meeting meetingWithoutParticipant = TestDataHelper.createTestMeeting(1L, "Test Meeting", "Test Description", organizer, timeSlot);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meetingWithoutParticipant));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> meetingService.acceptInvitation(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User 2 is not a participant in meeting 1");

        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(timeSlotRepository, never()).save(any());
        verify(notificationService, never()).sendMeetingAcceptance(any(), any());
    }

    @Test
    void acceptInvitation_WhenTimeSlotNotFound_ShouldThrowException() {
        Calendar emptyCalendar = new com.minidoodle.model.Calendar();
        emptyCalendar.setUser(participant);
        participant.setCalendar(emptyCalendar);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> meetingService.acceptInvitation(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("TimeSlot not found for participant 2 with the meeting 1");

        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(timeSlotRepository, never()).save(any());
        verify(notificationService, never()).sendMeetingAcceptance(any(), any());
    }

    @Test
    void removeParticipant_ShouldUpdateTimeSlotStatus() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(meetingRepository.save(any())).thenAnswer(invocation -> invocation.getRawArguments()[0]);

        timeSlot.setMeeting(meeting);
        timeSlot.setStatus(TimeSlotStatus.BUSY);

        MeetingDTO result = meetingService.removeParticipant(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(timeSlot.getStatus()).isEqualTo(TimeSlotStatus.AVAILABLE);
        verify(meetingRepository).save(meeting);
    }
} 