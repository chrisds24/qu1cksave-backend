package com.qu1cksave.qu1cksave_backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ResponseUserDto {
    private final UUID id;
    private final String name;
    private final String email;
    private final String[] roles;
    private final String accessToken;

    // TODO:
    //  When testing with .jsonPath, the order here matters. Not sure why since
    //    I already have @JsonProperty.
    //  - It might be the UserMapper causing this, since I'm using the
    //    constructor to set the values (since no setters for dto)
    public ResponseUserDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("roles") Object roles,
        // TODO: Does the frontend expect accessToken or access_token?
        //  - The job classes expect snakecase (since it made it easier for me to
        //     work on the Node.js when I didn't have to change them).
        @JsonProperty("access_token") String accessToken
    ) {
        try {
            this.id = id;
            this.name = name;
            this.email = email;

            ObjectMapper objectMapper = new ObjectMapper();
            if (roles instanceof String) {
                this.roles = objectMapper.readValue((String) roles, String[].class);
            } else {
                ArrayList arrLstRoles = (ArrayList) roles;
                String[] rolesArr = new String[arrLstRoles.size()];
                arrLstRoles.toArray(rolesArr);
                this.roles = rolesArr;
            }

            this.accessToken = accessToken;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String[] getRoles() { return roles; }
    public String getAccessToken() { return accessToken; }

    @Override
    public boolean equals(Object comparedObject) {
        // Variables in same memory location so they are the same
        if (this == comparedObject) {
            return true;
        }

        // if comparedObject is not a ResponseUserDto, can't be the same object
        if (!(comparedObject instanceof ResponseUserDto)) {
            return false;
        }

        ResponseUserDto comparedResponseUserDto = (ResponseUserDto) comparedObject;

        return Objects.equals(this.getId(), comparedResponseUserDto.getId()) &&
            Objects.equals(this.getName(), comparedResponseUserDto.getName()) &&
            Objects.equals(this.getEmail(), comparedResponseUserDto.getEmail()) &&
            Arrays.equals(this.getRoles(), comparedResponseUserDto.getRoles()) &&
            Objects.equals(this.getAccessToken(), comparedResponseUserDto.getAccessToken());
    }
}
