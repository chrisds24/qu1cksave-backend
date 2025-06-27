package com.qu1cksave.qu1cksave_backend.user;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<ResponseUserDto> login(
        @Valid @RequestBody CredentialsDto credentials
    ) {
        // TODO: In the Node.js version, the login and signup endpoints reject
        //   any request that doesn't have the API key, but the other endpoints
        //   including job, resume, and cover letter endpoints don't even
        //   though they should
        //  - Cloudflare already blocks requests that don't have the API key.
        //  - Though, I should add a filter here that checks it for every
        //    request including unauthenticated ones

        ResponseUserDto user = userService.login(credentials);

        return new ResponseEntity<ResponseUserDto>(
            user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }
}
