package com.qu1cksave.qu1cksave_backend.user;

import com.qu1cksave.qu1cksave_backend.exceptions.InvalidCredentialsException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLAddFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.SQLGetFailedException;
import com.qu1cksave.qu1cksave_backend.exceptions.UserAlreadyExistsException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Jwts;

import java.util.Date;

@Component
public class UserService {
    private final UserRepository userRepository;

    // Why was this called ACCESS_TOKEN again? For some reason, I used this
    //   name for the env var even though SECRET would be more appropriate
    private final String secret = System.getenv("ACCESS_TOKEN");

    public UserService(
        @Autowired UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResponseUserDto login(CredentialsDto credentials) {
//        return userRepository.findByEmail(
//            credentials.getEmail()
//        ).map(UserMapper::toResponseDto).orElse(null);
        User user;
        try {
            user = userRepository.findByEmail(
                credentials.getEmail()
            ).orElse(null);
        } catch(RuntimeException err) {
            throw new SQLGetFailedException(
                "Select user failed when logging in"
            );
        }

        // User with given email does not exist
        if (user == null) {
            return null;
        }

        // If passwords match, get the jwt and return it as accessToken
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(credentials.getPassword(), user.getPassword())) {
            // Create the signed JWT
            String accessToken = Jwts.builder()
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("roles", user.getRoles())
                // https://forums.oracle.com/ords/apexds/post/system-currenttimemillis-returns-time-in-utc-or-not-4286
                // - long values of Date are always in UTC
                // - However, Date.toString() uses the local timezone
                // https://docs.oracle.com/javase/8/docs/api/java/util/Date.html#Date-long-
                // - Date constructor taking in a long isn't deprecated
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
//                .expiration(Date.from(Instant.ofEpochSecond(4622470422L)))  // From tutorial (keep as reference)
                // https://javadoc.io/static/io.jsonwebtoken/jjwt-api/0.12.6/io/jsonwebtoken/JwtBuilder.html#signWith(K,%20io.jsonwebtoken.security.SecureDigestAlgorithm)
                .signWith(
                    // https://stackoverflow.com/questions/55102937/how-to-create-a-spring-security-key-for-signing-a-jwt-token
//                    Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), // If base 64 encoded
                    Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), // If base 64 encoded
                    // https://javadoc.io/static/io.jsonwebtoken/jjwt-api/0.12.6/io/jsonwebtoken/Jwts.SIG.html
                    Jwts.SIG.HS256
                )
                .compact();

            return UserMapper.toResponseDtoWithAccessToken(
                user,
                accessToken
            );
        } else {
            throw new InvalidCredentialsException(
                "Wrong password provided"
            );
        }
    }

    @Transactional
    public ResponseUserDto signup(RequestUserDto newUser) {
        User user;
        try {
            user = userRepository.findByEmail(
                newUser.getEmail()
            ).orElse(null);
        } catch(RuntimeException err) {
            throw new SQLGetFailedException(
                "Select user failed when signing up"
            );
        }

        if (user != null) {
            throw new UserAlreadyExistsException(
                "User with given email already exists"
            );
        }

        // Hash the password
        // Node.js version:
        //   const hashedPassword = bcrypt.hashSync(newUser.password, 10);
        // - Note that 10 is the salt for the Node.js version, while the 10
        //   here isn't a salt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String hashedPw = encoder.encode(newUser.getPassword());

        // Insert new user to database and return
        String[] roles = {"member"};
        User newUserEntity = UserMapper.createEntity(
            newUser,
            hashedPw,
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