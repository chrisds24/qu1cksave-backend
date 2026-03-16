package com.qu1cksave.qu1cksave_backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TestAuthHelpers {
    // Spring Boot loads the same configuration classes from src/main that my
    //   application uses. Therefore, I won't need to initialize Firebase Admin
    //   SDK in a test configuration file

    private final FirebaseAuth firebaseAuth;
    private final String firebaseWebApiKey = System.getenv("FIREBASE_WEB_API_KEY");

    public TestAuthHelpers(
        @Autowired FirebaseAuth firebaseAuth
    ) {
        this.firebaseAuth = firebaseAuth;

    }

    public String firebaseLogin(
        String firebase_uid
    ) throws FirebaseAuthException, JsonProcessingException {
        // Generate custom token (can't be verified via verifyToken)
        // - https://firebase.google.com/docs/auth/admin/create-custom-tokens#service_account_does_not_have_required_permissions
        //   + If the service account the Firebase Admin SDK is running as does
        //     not have the iam.serviceAccounts.signBlob permission, go to the
        //     link above for a solution
        // - https://firebase.google.com/docs/auth/admin/create-custom-tokens#failed_to_determine_service_account
        //   + "Failed to determine service account ID. Initialize the SDK with
        //      service account credentials..."
        //   + If you get an error message similar to the following, the
        //     Firebase Admin SDK has not been properly initialized
        String customToken = firebaseAuth.createCustomToken(firebase_uid);

        // Login via Firebase Auth by calling their API, then return the
        //   Firebase token (JWT) from that

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            String body = """
                {
                  "token":"%s",
                  "returnSecureToken":true
                }
                """.formatted(customToken);

            // https://firebase.google.com/docs/reference/rest/auth#section-verify-custom-token
            // - Source on how to call Firebase Auth backend REST API
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                    "https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=" + firebaseWebApiKey))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        // To see that each user is only being logged in once for a whole test run
        System.out.println("********** LOGGED IN AS " + firebase_uid);

        return json.get("idToken").asText();
    }
}

