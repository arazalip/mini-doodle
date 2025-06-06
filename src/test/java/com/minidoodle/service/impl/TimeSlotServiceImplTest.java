package com.minidoodle.service.impl;

import com.minidoodle.dto.TimeSlotDTO;
import com.minidoodle.helper.TestDataHelper;
import com.minidoodle.mapper.TimeSlotMapper;
import com.minidoodle.model.Calendar;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.TimeSlotStatus;
import com.minidoodle.model.User;
import com.minidoodle.repository.TimeSlotRepository;
import com.minidoodle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.minidoodle.mapper.TimeSlotMapper.toInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({TimeSlotMapper.class})
class TimeSlotServiceImplTest {

    @MockBean
    private TimeSlotRepository timeSlotRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private TimeSlotServiceImpl timeSlotService;

    private TimeSlot timeSlot;
    private TimeSlotDTO timeSlotDTO;
    private User user;
    private Calendar calendar;

    @BeforeEach
    void setUp() {
        calendar = TestDataHelper.createDefaultTestCalendar();
        user = TestDataHelper.createDefaultTestUser();
        user.setCalendar(calendar);
        timeSlot = TestDataHelper.createDefaultTestTimeSlot();
        timeSlotDTO = TestDataHelper.createDefaultTestTimeSlotDTO();
        calendar.setUser(user);
        timeSlot.setCalendar(calendar);
    }

    @Test
    void createTimeSlot_ShouldReturnCreatedTimeSlot() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);

        TimeSlotDTO result = timeSlotService.createTimeSlot(timeSlotDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(timeSlotDTO.getId());
        assertThat(result.getUserId()).isEqualTo(timeSlotDTO.getUserId());
        assertThat(result.getStatus()).isEqualTo(timeSlotDTO.getStatus());

        verify(timeSlotRepository).save(argThat(TimeSlot::isValid));
    }

    @Test
    void getTimeSlotById_WhenTimeSlotExists_ShouldReturnTimeSlot() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        TimeSlotDTO result = timeSlotService.getTimeSlotById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(timeSlotDTO.getId());
        verify(timeSlotRepository).findById(1L);
    }

    @Test
    void getTimeSlotById_WhenTimeSlotDoesNotExist_ShouldThrowException() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSlotService.getTimeSlotById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("TimeSlot not found with id: 1");

        verify(timeSlotRepository).findById(1L);
    }

    @Test
    void getTimeSlotsByUserId_ShouldReturnTimeSlots() {
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);
        when(timeSlotRepository.findByCalendarId(1L)).thenReturn(timeSlots);

        List<TimeSlotDTO> result = timeSlotService.getTimeSlotsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(timeSlotDTO.getId());
        verify(timeSlotRepository).findByCalendarId(1L);
    }

    @Test
    void getAvailableTimeSlots_WhenUserExists_ShouldReturnAvailableTimeSlots() {
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(timeSlotRepository.findAvailableTimeSlots(calendar.getId())).thenReturn(timeSlots);

        List<TimeSlotDTO> result = timeSlotService.getAvailableTimeSlots(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(timeSlotDTO.getId());
        verify(userRepository).findById(1L);
        verify(timeSlotRepository).findAvailableTimeSlots(calendar.getId());
    }

    @Test
    void getAvailableTimeSlots_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSlotService.getAvailableTimeSlots(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: 1");

        verify(userRepository).findById(1L);
        verify(timeSlotRepository, never()).findAvailableTimeSlots(any());
    }

    @Test
    void getTimeSlotsInRange_ShouldReturnTimeSlots() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(timeSlotRepository.findAvailableSlotsInTimeRange(1L, toInstant(startTime), toInstant(endTime))).thenReturn(timeSlots);

        List<TimeSlotDTO> result = timeSlotService.getTimeSlotsInRange(1L, startTime, endTime);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(timeSlotDTO.getId());
        verify(timeSlotRepository).findAvailableSlotsInTimeRange(1L, toInstant(startTime), toInstant(endTime));
    }

    @Test
    void markTimeSlotAsBusy_WhenTimeSlotExists_ShouldUpdateStatus() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(timeSlot)).thenReturn(timeSlot);

        TimeSlotDTO result = timeSlotService.markTimeSlotAsBusy(1L);

        assertThat(result).isNotNull();
        assertThat(timeSlot.getStatus()).isEqualTo(TimeSlotStatus.BUSY);
        verify(timeSlotRepository).findById(1L);
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void markTimeSlotAsAvailable_WhenTimeSlotExists_ShouldUpdateStatus() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(timeSlot)).thenReturn(timeSlot);

        TimeSlotDTO result = timeSlotService.markTimeSlotAsAvailable(1L);

        assertThat(result).isNotNull();
        assertThat(timeSlot.getStatus()).isEqualTo(TimeSlotStatus.AVAILABLE);
        verify(timeSlotRepository).findById(1L);
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void markTimeSlotAsBooked_WhenTimeSlotExists_ShouldUpdateStatus() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(timeSlot)).thenReturn(timeSlot);

        TimeSlotDTO result = timeSlotService.markTimeSlotAsBooked(1L);

        assertThat(result).isNotNull();
        assertThat(timeSlot.getStatus()).isEqualTo(TimeSlotStatus.BOOKED);
        verify(timeSlotRepository).findById(1L);
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void updateTimeSlot_WhenTimeSlotExists_ShouldReturnUpdatedTimeSlot() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);

        TimeSlotDTO result = timeSlotService.updateTimeSlot(1L, timeSlotDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(timeSlotDTO.getId());
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void updateTimeSlot_WhenTimeSlotDoesNotExist_ShouldThrowException() {
        assertThatThrownBy(() -> timeSlotService.updateTimeSlot(1L, timeSlotDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("TimeSlot not found with id: 1");

        verify(timeSlotRepository, never()).save(any());
    }

    @Test
    void deleteTimeSlot_WhenTimeSlotExists_ShouldDeleteTimeSlot() {
        when(timeSlotRepository.existsById(1L)).thenReturn(true);
        doNothing().when(timeSlotRepository).deleteById(1L);

        timeSlotService.deleteTimeSlot(1L);

        verify(timeSlotRepository).existsById(1L);
        verify(timeSlotRepository).deleteById(1L);
    }

    @Test
    void deleteTimeSlot_WhenTimeSlotDoesNotExist_ShouldThrowException() {
        when(timeSlotRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> timeSlotService.deleteTimeSlot(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("TimeSlot not found with id: 1");

        verify(timeSlotRepository).existsById(1L);
        verify(timeSlotRepository, never()).deleteById(any());
    }
} 