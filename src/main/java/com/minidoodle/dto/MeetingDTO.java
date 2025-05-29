package com.minidoodle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class MeetingDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Organizer ID is required")
    private Long organizerId;
    
    @NotNull(message = "Time slot ID is required")
    private Long timeSlotId;
    
    private Set<Long> participantIds;
} 