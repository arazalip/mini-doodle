package com.minidoodle.service.impl;

import com.minidoodle.dto.MeetingDTO;
import com.minidoodle.helper.TestDataHelper;
import com.minidoodle.mapper.MeetingMapper;
import com.minidoodle.model.Meeting;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.User;
import com.minidoodle.repository.MeetingRepository;
import com.minidoodle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingMapper meetingMapper;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private Meeting meeting;
    private MeetingDTO meetingDTO;
    private User participant;

    @BeforeEach
    void setUp() {
        User organizer = TestDataHelper.createDefaultTestUser();
        participant = TestDataHelper.createTestUser(2L, "participant@example.com", "Participant");
        TimeSlot timeSlot = TestDataHelper.createDefaultTestTimeSlot();
        meeting = TestDataHelper.createTestMeeting(1L, "Test Meeting", "Test Description", organizer, timeSlot, participant);
        meetingDTO = TestDataHelper.createTestMeetingDTO(1L, "Test Meeting", "Test Description", organizer.getId(), timeSlot.getId(), participant.getId());
    }

    @Test
    void createMeeting_ShouldReturnCreatedMeeting() {
        when(meetingMapper.toEntity(any(MeetingDTO.class))).thenReturn(meeting);
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        when(meetingMapper.toDTO(any(Meeting.class))).thenReturn(meetingDTO);

        MeetingDTO result = meetingService.createMeeting(meetingDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        assertThat(result.getTitle()).isEqualTo(meetingDTO.getTitle());
        assertThat(result.getOrganizerId()).isEqualTo(meetingDTO.getOrganizerId());

        verify(meetingMapper).toEntity(meetingDTO);
        verify(meetingRepository).save(meeting);
        verify(meetingMapper).toDTO(meeting);
    }

    @Test
    void getMeetingById_WhenMeetingExists_ShouldReturnMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(meetingMapper.toDTO(meeting)).thenReturn(meetingDTO);

        MeetingDTO result = meetingService.getMeetingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findById(1L);
        verify(meetingMapper).toDTO(meeting);
    }

    @Test
    void getMeetingById_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getMeetingById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).findById(1L);
        verify(meetingMapper, never()).toDTO(any());
    }

    @Test
    void getMeetingsByOrganizerId_ShouldReturnMeetings() {
        List<Meeting> meetings = Collections.singletonList(meeting);
        when(meetingRepository.findByOrganizerId(1L)).thenReturn(meetings);
        when(meetingMapper.toDTO(meeting)).thenReturn(meetingDTO);

        List<MeetingDTO> result = meetingService.getMeetingsByOrganizerId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findByOrganizerId(1L);
        verify(meetingMapper).toDTO(meeting);
    }

    @Test
    void getMeetingsByParticipantId_ShouldReturnMeetings() {
        List<Meeting> meetings = Collections.singletonList(meeting);
        when(meetingRepository.findByParticipantId(2L)).thenReturn(meetings);
        when(meetingMapper.toDTO(meeting)).thenReturn(meetingDTO);

        List<MeetingDTO> result = meetingService.getMeetingsByParticipantId(2L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findByParticipantId(2L);
        verify(meetingMapper).toDTO(meeting);
    }

    @Test
    void addParticipant_WhenMeetingAndUserExist_ShouldAddParticipant() {
        User newParticipant = new User();
        newParticipant.setId(3L);
        newParticipant.setEmail("new@example.com");
        newParticipant.setName("New Participant");

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newParticipant));
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        when(meetingMapper.toDTO(meeting)).thenReturn(meetingDTO);

        MeetingDTO result = meetingService.addParticipant(1L, 3L);

        assertThat(result).isNotNull();
        assertThat(meeting.getParticipants()).contains(newParticipant);
        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(3L);
        verify(meetingRepository).save(meeting);
        verify(meetingMapper).toDTO(meeting);
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
        when(meetingMapper.toDTO(meeting)).thenReturn(meetingDTO);

        MeetingDTO result = meetingService.removeParticipant(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(meeting.getParticipants()).doesNotContain(participant);
        verify(meetingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(meetingRepository).save(meeting);
        verify(meetingMapper).toDTO(meeting);
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
    void getMeetingsByTimeSlotId_ShouldReturnMeetings() {
        Set<Meeting> meetings = new HashSet<>(Collections.singletonList(meeting));
        when(meetingRepository.findByTimeSlotId(1L)).thenReturn(meetings);
        when(meetingMapper.toDTO(meeting)).thenReturn(meetingDTO);

        Set<MeetingDTO> result = meetingService.getMeetingsByTimeSlotId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).findByTimeSlotId(1L);
        verify(meetingMapper).toDTO(meeting);
    }

    @Test
    void updateMeeting_WhenMeetingExists_ShouldReturnUpdatedMeeting() {
        when(meetingRepository.existsById(1L)).thenReturn(true);
        when(meetingMapper.toEntity(any(MeetingDTO.class))).thenReturn(meeting);
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        when(meetingMapper.toDTO(any(Meeting.class))).thenReturn(meetingDTO);

        MeetingDTO result = meetingService.updateMeeting(1L, meetingDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(meetingDTO.getId());
        verify(meetingRepository).existsById(1L);
        verify(meetingMapper).toEntity(meetingDTO);
        verify(meetingRepository).save(meeting);
        verify(meetingMapper).toDTO(meeting);
    }

    @Test
    void updateMeeting_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> meetingService.updateMeeting(1L, meetingDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).existsById(1L);
        verify(meetingMapper, never()).toEntity(any());
        verify(meetingRepository, never()).save(any());
    }

    @Test
    void deleteMeeting_WhenMeetingExists_ShouldDeleteMeeting() {
        when(meetingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(meetingRepository).deleteById(1L);

        meetingService.deleteMeeting(1L);

        verify(meetingRepository).existsById(1L);
        verify(meetingRepository).deleteById(1L);
    }

    @Test
    void deleteMeeting_WhenMeetingDoesNotExist_ShouldThrowException() {
        when(meetingRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> meetingService.deleteMeeting(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Meeting not found with id: 1");

        verify(meetingRepository).existsById(1L);
        verify(meetingRepository, never()).deleteById(any());
    }
} 