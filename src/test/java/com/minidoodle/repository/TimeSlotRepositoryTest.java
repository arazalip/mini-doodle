package com.minidoodle.repository;

import com.minidoodle.model.Calendar;
import com.minidoodle.model.TimeSlot;
import com.minidoodle.model.TimeSlotStatus;
import com.minidoodle.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TimeSlotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private User user;
    private Calendar calendar;
    private Instant now;
    private Instant oneHourLater;
    private Instant twoHoursLater;
    private Instant threeHoursLater;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        entityManager.persist(user);

        calendar = user.getCalendar();
        entityManager.flush();

        now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        oneHourLater = now.plus(1, ChronoUnit.HOURS);
        twoHoursLater = now.plus(2, ChronoUnit.HOURS);
        threeHoursLater = now.plus(3, ChronoUnit.HOURS);
    }

    @Test
    void findAvailableTimeSlotsByUserIdAndTimeRange_ShouldFindOverlappingSlots() {
        TimeSlot slot = createTimeSlot(now.minus(30, ChronoUnit.MINUTES), oneHourLater, TimeSlotStatus.AVAILABLE);
        entityManager.persist(slot);
        entityManager.flush();

        List<TimeSlot> result = timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(
                user.getId(), now, oneHourLater, TimeSlotStatus.AVAILABLE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(slot.getId());
    }

    @Test
    void findAvailableTimeSlotsByUserIdAndTimeRange_ShouldFindContainedSlots() {
        TimeSlot slot = createTimeSlot(oneHourLater, twoHoursLater, TimeSlotStatus.AVAILABLE);
        entityManager.persist(slot);
        entityManager.flush();

        List<TimeSlot> result = timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(
                user.getId(), now, threeHoursLater, TimeSlotStatus.AVAILABLE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(slot.getId());
    }

    @Test
    void findAvailableTimeSlotsByUserIdAndTimeRange_ShouldNotFindBookedSlots() {
        TimeSlot availableSlot = createTimeSlot(now, oneHourLater, TimeSlotStatus.AVAILABLE);
        TimeSlot bookedSlot = createTimeSlot(oneHourLater, twoHoursLater, TimeSlotStatus.BOOKED);
        entityManager.persist(availableSlot);
        entityManager.persist(bookedSlot);
        entityManager.flush();

        List<TimeSlot> result = timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(
                user.getId(), now, twoHoursLater, TimeSlotStatus.AVAILABLE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(availableSlot.getId());
    }

    @Test
    void findAvailableTimeSlotsByUserIdAndTimeRange_ShouldReturnEmptyListForNoMatches() {
        TimeSlot slot = createTimeSlot(threeHoursLater, threeHoursLater.plus(1, ChronoUnit.HOURS), TimeSlotStatus.AVAILABLE);
        entityManager.persist(slot);
        entityManager.flush();

        List<TimeSlot> result = timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(
                user.getId(), now, twoHoursLater, TimeSlotStatus.AVAILABLE);

        assertThat(result).isEmpty();
    }

    @Test
    void findAvailableTimeSlotsByUserIdAndTimeRange_ShouldFindMultipleOverlappingSlots() {
        TimeSlot slot1 = createTimeSlot(now.minus(30, ChronoUnit.MINUTES), oneHourLater, TimeSlotStatus.AVAILABLE);
        TimeSlot slot2 = createTimeSlot(now, twoHoursLater, TimeSlotStatus.AVAILABLE);
        TimeSlot slot3 = createTimeSlot(oneHourLater, threeHoursLater, TimeSlotStatus.AVAILABLE);
        entityManager.persist(slot1);
        entityManager.persist(slot2);
        entityManager.persist(slot3);
        entityManager.flush();

        List<TimeSlot> result = timeSlotRepository.findAvailableTimeSlotsByUserIdAndTimeRange(
                user.getId(), now, twoHoursLater, TimeSlotStatus.AVAILABLE);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(TimeSlot::getId)
                .containsExactlyInAnyOrder(slot1.getId(), slot2.getId(), slot3.getId());
    }

    private TimeSlot createTimeSlot(Instant startTime, Instant endTime, TimeSlotStatus status) {
        TimeSlot slot = new TimeSlot();
        slot.setCalendar(calendar);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(status);
        return slot;
    }
} 