package com.qu1cksave.qu1cksave_backend.user;

import com.qu1cksave.qu1cksave_backend.exceptions.SQLAddFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {
    private final UserRepository userRepository;

    private final String secret = System.getenv("ACCESS_TOKEN");

    public UserService(
        @Autowired UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    // Users are signed up through auto-provisioning. When they make an
    //   authenticated request, they are signed up if their email is
    //   already verified and they don't have an entry in the database
    //
    // ResponseUserDto here should contain firebase_uid, email, name
    @Transactional
    public ResponseUserDto signup(RequestUserDto newUser) {
        // Before signing up a user through auto-provisioning, existence is
        //   already checked before calling this signup function. No need to
        //   check existence again here.

        // TODO: Use this code to lookup a user's existence. I could have a
        //   getUserByEmail function.
//        User user;
//        try {
//            user = userRepository.findByEmail(
//                newUser.getEmail()
//            ).orElse(null);
//        } catch(RuntimeException err) {
//            throw new SQLGetFailedException(
//                "Select user failed when signing up"
//            );
//        }
//
//        if (user != null) {
//            throw new UserAlreadyExistsException(
//                "User with given email already exists"
//            );
//        }

        // Insert new user to database and return
        String[] roles = {"member"};
        User newUserEntity = UserMapper.createEntity(
            newUser,
            roles
        );
        try {
            return UserMapper.toResponseDto(userRepository.save(newUserEntity));
        } catch (RuntimeException err) {
            throw new SQLAddFailedException(
                "Save new user failed", err
            );
        }
    }
}

// -------------- BCrypt without Spring Security -----------------
// - spring-security-crypto (From Google AI Overview)
//   -- Can instantiate a BCryptPasswordEncoder
// - https://stackoverflow.com/questions/73940887/best-way-for-spring-boot-password-encryption-not-using-bcryptpasswordencoder
//   -- Suggests spring-security-crypto and BCryptPasswordEncoder
// - https://stackoverflow.com/questions/56249159/use-bcrypt-hashing-function-in-spring-boot-without-all-the-overkill-security
//   -- If using Spring Security, add the code below to not include the auto configurations
//      + @SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
// - https://docs.spring.io/spring-security/reference/features/integrations/cryptography.html
//   -- Example on how to use spring-security-crypto and BCryptPasswordEncoder
// *** I'm going with spring-security-crypto and BCryptPasswordEncoder

// --------------------------- JWT ------------------------
//  - https://springframework.guru/jwt-authentication-in-spring-microservices-jwt-token/
//    -- Doesn't use Spring Security
//    -- Really good. Even has some code on how to work with JWT
//    -- This one uses a filter for authentication too (to check the jwt in
//       the header)
//       + In addition, I need a way to extract the authorities from the jwt
//  - https://www.baeldung.com/java-json-web-tokens-jjwt
//    -- Claims are the payload
//    -- Really good tutorials
//  - Javadoc for JJWT / jsonwebtoken.Jwts
//    -- https://javadoc.io/doc/io.jsonwebtoken/jjwt-api/0.11.2/io/jsonwebtoken/Jwts.html
//    -- https://javadoc.io/static/io.jsonwebtoken/jjwt-api/0.12.6/io/jsonwebtoken/JwtBuilder.html
//  - https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
//    -- Also has info when working with JWTs
//
// io.jsonwebtoken.jwts dependency
// - https://stackoverflow.com/questions/74828917/maven-dependencies-for-jjwt-json-web-token-on-a-spring-boot-project
// - https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
//
// Signing the jwt
// - https://stackoverflow.com/questions/73576686/what-substitute-can-i-use-for-java-springs-jwts-signwith-deprecated-method
// - https://www.baeldung.com/spring-security-sign-jwt-token
// - https://stackoverflow.com/questions/55102937/how-to-create-a-spring-security-key-for-signing-a-jwt-token
// - https://stackoverflow.com/questions/8571501/how-to-check-whether-a-string-is-base64-encoded-or-not
//
// Date in expiration
//  - Ex. .expiration(new Date(System.currentTimeMillis() + 1000 * 60 + 24)) seems better
//    -- From: https://stackoverflow.com/questions/73576686/what-substitute-can-i-use-for-java-springs-jwts-signwith-deprecated-method
//    -- I'll use + 1000 * 60 * 60 * 2
//       + 1000 ms in a second, 60 seconds in a minute, 60 minutes in an hour, times 2 (for 2 hours)
//  - https://forums.oracle.com/ords/apexds/post/system-currenttimemillis-returns-time-in-utc-or-not-4286
//    -- long values of Date are always in UTC
//    -- However, Date.toString() uses the local timezone
//  - https://stackoverflow.com/questions/17271039/does-system-currenttimemillis-return-utc-time