package com.qu1cksave.qu1cksave_backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserMapper {
    public static ResponseUserDto toResponseDto(User entity) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return new ResponseUserDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRoles() != null ? objectMapper.writeValueAsString(entity.getRoles()) : null,
                null // Entity doesn't have an accessToken
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
