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

        String firebaseUid = decodedToken.getUid();
        boolean emailVerified = decodedToken.isEmailVerified();
        String name = decodedToken.getName();
        String email = decodedToken.getEmail();

        // No need for null check since result of verifyIdToken can't be null
        if (!emailVerified) {
            throw new CustomFilterException("User's email not yet verified.");
        }

        // Name could be empty due to Firebase signup error in the frontend,
        //   where updateProfile fails to update the user's displayName in
        //   Firebase. Need to default to "No Name" here since the DB has
        //   name as NOT NULL.
        // The Firebase displayName (used by sidebar in frontend) and the
        //   DB name will currently be out of sync here, but the user can
        //   just update it in profile settings (TO DO LATER) in which they
        //   can update their name in the DB here and in Firebase (Do this in
        //   a transaction to rollback if Firebase update fails)
        // Probably don't even need name.isEmpty(), but just being safe
        if (name == null || name.isEmpty()) {
            name = "No Name";
        }

        ResponseUserDto user = userService.getUserByFirebaseUid(firebaseUid);
        // User with given firebaseUid not found in DB. Need to signup the user
        if (user == null) {
            user = userService.signup(new RequestUserDto(
                name,
                email,
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
