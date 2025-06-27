package com.qu1cksave.qu1cksave_backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

// Sent by user when logging in
public class CredentialsDto {
    @NotNull
    private final String email;
    @NotNull
    private final String password;

    public CredentialsDto(
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
    ) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
