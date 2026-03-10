package com.qu1cksave.qu1cksave_backend.filters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
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

    // NOTE: Helper function used to update Firebase info for initial testing
    private UserRecord updateFirebaseInfo(
        boolean emailVerified,
        String nameFromToken,
        String firebaseUid,
        String name
    ) throws FirebaseAuthException {
        boolean emailNotVerified = !emailVerified;
        boolean userNameNotSet = nameFromToken == null;

        // Only make a network request to update user if email is not verified
        //   or name is not set
        if (emailNotVerified || userNameNotSet) {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(firebaseUid);
            // Set email_verified to true if false
            if (emailNotVerified) {
                request.setEmailVerified(true);
            }
            // Set name if not yet set
            if (userNameNotSet) {
                request.setDisplayName(name);
            }
            // Make the update request
            UserRecord userRecord = firebaseAuth.updateUser(request);

            // IMPORTANT:
            // - In the tests, each user logs in once.
            // - Notice that an update to the Firebase user is made here, but
            //   the token wouldn't have that updated info.
            // - As a result, the same user would end up making this request to
            //   updated their info even though it's already updated in Firebase
            // - SOLUTION: Revoke the token here
            //   + NOTE: This will cause a valid test to fail the first time
            //     for a user due to their token getting invalidated.
            //   + But for another test run attempt, tests should work as
            //     normal for the user since they'll always have the updated
            //     token.
            //   + HOWEVER: verifyToken needs to detect revocation. So I'll end
            //     up having to make a network request anyway...UNLESS (SEE BELOW)
            // - https://firebase.google.com/docs/auth/admin/manage-sessions#detect_id_token_revocation
            //   + Set up Firebase Security Rules that check for revocation
            //     rather than using the Admin SDK to make the check
            //   + Too complicated for my use case
            // - SOLUTION:

            firebaseAuth.revokeRefreshTokens(firebaseUid);

            return userRecord;
        } else {
            return null;
        }
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

        // ****************************************************************
        // ***************** COMMENT THIS OUT LATER ***********************
        // ****************************************************************
        // NOTE: This section was used to set Firebase info for the users
        //   below since there's no option to manually set them in Firebase

        // 1st time:
        // - email not verified in both Firebase and the token, so the code
        //   inside the if statement executes
        // - passes this check since token is valid
        // - HOWEVER, token is revoked once Firebase update is made below
        // - *** EXTRA INFO: Request passes the entire filter here, since we
        //   get the updated valid info from the updateUser call
        // 2nd time:
        // - email verified in Firebase but not in the outdated token, so the
        //   code inside the if statement still executes
        // - However, verifyToken throws an exception since the the outdated
        //   has been revoked
        // - Test fails. Need to start the whole test run again
        // - *** EXTRA INFO: Request fails the filter here
        // 3rd time and later:
        // - Email is finally verified in both the token and Firebase, so the
        //   code inside the if statement won't execute anymore
        // *** EXTRA INFO: Request passes the entire filter here, since we have
        // the valid info from the first verifyToken call above
        //
        // OVERALL
        // - I'll end up making 2 network requests from verifyToken for each user
        //   listed below
        // TODO:
        //  - Remove this before adding unverified email test
        if (!emailVerified) {
            try {
                // https://firebase.google.com/docs/auth/admin/manage-sessions#detect_id_token_revocation_in_the_sdk
                // - I don't need the result here. I just need the filter to
                //   check if the token has been revoked, causing the filter to
                //   throw an exception
                firebaseAuth.verifyIdToken(jwt, true);
            } catch (FirebaseAuthException err) {
                throw new CustomFilterException(
                    "Revoked token",
                    err
                );
            }
        }

        final String mollyFirebaseUid = "Qc5s1NgoczgltJYpA1MoY8Zpxc82";
        final String annaFirebaseUid = "cM3Is9tk1KWiDJXUfy43kFhmOKH2";
        final String goatFirebaseUid = "VYMAKQ2AA8PgsutJsSWpB0f4aZA2";
        final String nobbyFirebaseUid = "jVJi6nY1etMjwzlk0WqQ5m7Xhs63";
        final String chrisFirebaseUid = "edaoT5YmdTTwVZlX8d90Qzh5aQ32";

        UserRecord userRecord = null;

        // If firebaseUid is one of the above ids
        try {
            switch (firebaseUid) {
                case mollyFirebaseUid:
                    userRecord = updateFirebaseInfo(emailVerified, name, firebaseUid, "Molly Member");
                    break;
                case annaFirebaseUid:
                    userRecord = updateFirebaseInfo(emailVerified, name, firebaseUid, "Anna Admin");
                    break;
                case goatFirebaseUid:
                    userRecord = updateFirebaseInfo(emailVerified, name, firebaseUid, "Goat User");
                    break;
                case nobbyFirebaseUid:
                    userRecord = updateFirebaseInfo(emailVerified, name, firebaseUid, "Nobby Nobody");
                    break;
                case chrisFirebaseUid:
                    userRecord = updateFirebaseInfo(emailVerified, name, firebaseUid, "Christian Delos Santos");
                    break;
                default:
                    break;
            }
        } catch (FirebaseAuthException err) {
            throw new CustomFilterException(
                "FirebaseAuthException when updating Firebase user", err);
        }

        // If an update was made, use the updated info
        if (userRecord != null) {
            emailVerified = userRecord.isEmailVerified();
            name = userRecord.getDisplayName();
        }

        // ****************************************************************
        // ****************************************************************
        // ****************************************************************

        // No need for null check since result of verifyIdToken can't be null
        if (!emailVerified) {
            throw new CustomFilterException("User's email not yet verified.");
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
