package com.qu1cksave.qu1cksave_backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.UUID;

public class ResponseUserDto {
    private final UUID id;
    private final String email;
    private final String name;
    private final String[] roles;
    private final String accessToken;

    public ResponseUserDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("roles") Object roles,
        // TODO: Does the frontend expect accessToken or access_token?
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
}
