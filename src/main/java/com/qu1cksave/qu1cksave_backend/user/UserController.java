package com.qu1cksave.qu1cksave_backend.user;

import com.qu1cksave.qu1cksave_backend.job.ResponseJobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    // Using a query param since /user/<user's id> is how to get one user
    //   by an id.
    // Makes sense to query if searching/filtering a user through other props
    @GetMapping()
    public ResponseEntity<ResponseUserDto> getOneByFirebaseUid(
        @RequestParam("firebaseuid") String firebaseUid
    ) {
        ResponseUserDto user = userService.getUserByFirebaseUid(firebaseUid);
        return new ResponseEntity<ResponseUserDto>(
            user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }
}
