package com.qu1cksave.qu1cksave_backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

// Sent by user when signing up
public class RequestUserDto {
    @NotNull
    private final String name;
    @NotNull
    private final String email;
    @NotNull
    private final String password;

    public RequestUserDto(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
