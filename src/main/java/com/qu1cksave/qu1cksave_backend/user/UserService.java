package com.qu1cksave.qu1cksave_backend.user;

import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {
    private final UserRepository userRepository;

    public UserService(
        @Autowired UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseUserDto login(CredentialsDto credentials) {
//        return userRepository.findByEmail(
//            credentials.getEmail()
//        ).map(UserMapper::toResponseDto).orElse(null);
        User dbUser;
        try {
            dbUser = userRepository.findByEmail(
                credentials.getEmail()
            ).orElse(null);
        } catch(RuntimeException err) {
            throw new SQLGetFailedException(
                "Select user failed when logging in"
            );
        }

        if (dbUser == null) {  // User with given email does not exist
            return null;
        }

        // If passwords match, get the jwt and return it as accessToken




    }
}
