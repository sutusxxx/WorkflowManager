package com.sutusxxx.user;

import com.sutusxxx.user.model.UserSummaryDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    private final ModelMapper mapper;

    public UserConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }
    public UserSummaryDTO convertToSummaryDTO(User user) {
        return mapper.map(user, UserSummaryDTO.class);
    }
}
