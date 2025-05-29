package com.minidoodle.mapper;

import com.minidoodle.dto.UserDTO;
import com.minidoodle.model.User;
import com.minidoodle.model.Calendar;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        return user;
    }

    public void updateEntity(User user, UserDTO dto, Calendar calendar) {
        if (user == null || dto == null) return;

        user.setEmail(dto.getEmail());
        user.setName(dto.getName());

        if (calendar != null) {
            user.setCalendar(calendar);
        }
    }
} 