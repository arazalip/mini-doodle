package com.minidoodle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "time_slots")
@Data
@ToString(exclude = "meeting")
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;

    @OneToOne(mappedBy = "timeSlot", cascade = CascadeType.ALL)
    private Meeting meeting;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Transient
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    public boolean isValid() {
        return startTime != null && endTime != null && 
               !startTime.isAfter(endTime) && 
               !startTime.equals(endTime);
    }

    public boolean overlaps(TimeSlot other) {
        return !this.endTime.isBefore(other.startTime) && 
               !this.startTime.isAfter(other.endTime);
    }

    public boolean contains(Instant instant) {
        return !instant.isBefore(startTime) && !instant.isAfter(endTime);
    }

    public void setDuration(Duration duration) {
        if (startTime == null) {
            throw new IllegalStateException("Start time must be set before setting duration");
        }
        this.endTime = startTime.plus(duration);
    }
}