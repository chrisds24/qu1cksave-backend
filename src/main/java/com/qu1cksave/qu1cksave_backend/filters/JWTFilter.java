package com.qu1cksave.qu1cksave_backend.filters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.qu1cksave.qu1cksave_backend.exceptions.CustomFilterException;
import com.qu1cksave.qu1cksave_backend.user.RequestUserDto;
import com.qu1cksave.qu1cksave_backend.user.ResponseUserDto;
import com.qu1cksave.qu1cksave_backend.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// Checks the provided JWT in the auth header
public class JWTFilter extends OncePerRequestFilter {
    private final FirebaseAuth firebaseAuth;
    private final UserService userService;

    public JWTFilter(
        @Autowired FirebaseAuth firebaseAuth,
        @Autowired UserService userService
    ) {
        this.firebaseAuth = firebaseAuth;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String jwt = (String) req.getAttribute("jwt");

        if (jwt == null || jwt.isEmpty()) {
            throw new CustomFilterException("Missing JWT");
        }

        // Steps:
        // 1.) Verify if the Firebase token provided (a JWT) is valid
        // 2.) If valid, check if the user's email is verified
        // 3.) If email is verified, check user's existence in the DB
        //     - First, find the user by firebaseUid
        //     - If user with the firebaseUid from the token doesn't exist,
        //       then sign up the user using the details from the token
        //     - If the user exists, no need to do anything else
        // 4.) Regardless if the user exists already or has been signed up,
        //     get the user's id and roles from the result of the database
        //     queries and attach those to the request
        // 5.) Go to the next filter in the filter chain

        FirebaseToken decodedToken = null;
        try {
            decodedToken = firebaseAuth.verifyIdToken(jwt);
        } catch (FirebaseAuthException err) {
            throw new CustomFilterException(
                "Exception occurred during verifyIdToken",
                err
            );
        }

        // TODO: For the users I'm testing and also the existing ones in the DB
        //   such as my own email. I need to:
        //  - Set name here
        //    + *** IMPORTANT ***
        //      * I might consider just getting the name from the backend.
        //        For example, if someone decides to change their name, it
        //        won't immediately reflect changes unless they logout if I'm
        //        getting the name from the token (which would be outdated)
        //    + Though, I should still just ask for their name upon signup.
        //  - Set emailVerified here
        //  Users to set:
        //  + molly@books.com       Molly Member
        //  + anna@books.com        Anna Admin
        //  + nobby@books.com       Nobby Nobody
        //  + goatuser@books.com    Goat User
        //    * The only user w/o a DB entry, which will be used to test signup
        //  + My own email
        //  Refer to https://console.firebase.google.com/project/qu1cksave/authentication/users
        //    for the uids
        //  I can use Admin SDK to set the displayName and email_verified for
        //    Firebase here.
        //  *** NOTE: The users above won't be signed up to my dev/test DB
        //    since they already have entries
        //  - But when testing, I need to have an existing Firebase user that
        //    doesn't have an entry in my DB here. (which would be Goat User)

        // No need for null check since result of verifyIdToken can't be null
        if (!decodedToken.isEmailVerified()) {
            throw new CustomFilterException("User's email not yet verified.");
        }

        String firebaseUid = decodedToken.getUid();
        ResponseUserDto user = userService.getUserByFirebaseUid(firebaseUid);
        // User with given firebaseUid not found in DB. Need to signup the user
        if (user == null) {
            user = userService.signup(new RequestUserDto(
                decodedToken.getName(),
                decodedToken.getEmail(),
                firebaseUid
            ));
        }

        // Set the userId and the roles
        // NOTE: If I ever need more than the userId, I can set it here
        UUID userId = user.getId();
        String[] roles = user.getRoles();
        if (userId == null) {
            throw new CustomFilterException("Missing userId.");
        }
        req.setAttribute("userId", userId);
        req.setAttribute("roles", roles);

        filterChain.doFilter(req, res);
    }

    // As of the addition of Firebase Authentication, there are no longer login
    //   and signup endpoints. Keeping this for reference
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest req) {
//        String path = req.getRequestURI();
//        return path.startsWith("/api/v0/user/login") || path.startsWith("/api/v0/user/signup");
//    }
}
