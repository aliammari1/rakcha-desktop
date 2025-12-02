package com.esprit.utils;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.httpclient.ahc.AhcHttpClientConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum SignInGoogle {
    ;
    private static final Logger LOGGER = Logger.getLogger(SignInGoogle.class.getName());
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SECRET_STATE = "secret" + RANDOM.nextInt(999_999);
    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final Object SERVICE_LOCK = new Object();
    private static volatile OAuth20Service service;
    private static volatile Map<String, Object> lastUserInfo;

    /**
     * Initialize Google Sign-In flow
     *
     * @return Authorization URL for the user to visit
     * @throws InterruptedException if the operation is interrupted
     * @throws ExecutionException   if the operation fails
     * @throws IOException          if there's an I/O error
     *                              if required environment variables are missing
     */
    public static String signInWithGoogle() throws InterruptedException, ExecutionException, IOException {
        // Initialize service if not already done
        if (service == null) {
            synchronized (SERVICE_LOCK) {
                if (service == null) {
                    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                    String clientId = dotenv.get("GOOGLE_CLIENT_ID");
                    String clientSecret = dotenv.get("GOOGLE_CLIENT_SECRET");

                    if (clientId == null || clientSecret == null) {
                        throw new IllegalStateException("Google OAuth credentials not found in .env file");
                    }

                    HttpClientConfig clientConfig = new AhcHttpClientConfig(new DefaultAsyncHttpClientConfig.Builder()
                        .setMaxConnections(5).setRequestTimeout(Duration.ofMillis(10_000))
                        .setPooledConnectionIdleTimeout(Duration.ofMillis(1_000)).setReadTimeout(Duration.ofMillis(1_000))
                        .build());

                    service = new ServiceBuilder(clientId).apiSecret(clientSecret).defaultScope("profile email")
                        .callback("urn:ietf:wg:oauth:2.0:oob").httpClientConfig(clientConfig).build(GoogleApi20.instance());
                }
            }
        }

        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("access_type", "offline");
        additionalParams.put("prompt", "consent");

        String authorizationUrl = service.createAuthorizationUrlBuilder().state(SECRET_STATE)
            .additionalParams(additionalParams).build();

        LOGGER.info("Authorization URL generated successfully");
        return authorizationUrl;
    }

    /**
     * Complete the OAuth flow with the authorization code
     *
     * @param code The authorization code from Google
     * @return true if authentication was successful
     * @throws IOException          if there's an I/O error
     * @throws ExecutionException   if the operation fails
     * @throws InterruptedException if the operation is interrupted
     */
    public static boolean verifyAuthUrl(final String code)
        throws IOException, ExecutionException, InterruptedException {
        try {
            // Ensure service is initialized
            if (service == null) {
                LOGGER.warning("OAuth service not initialized. Please call signInWithGoogle() first.");
                return false;
            }

            if (code == null || code.trim().isEmpty()) {
                LOGGER.warning("Authorization code is empty or null");
                return false;
            }

            LOGGER.info("Attempting to get access token with code: " + code.substring(0, Math.min(10, code.length())) + "...");
            OAuth2AccessToken accessToken = service.getAccessToken(code);
            LOGGER.info("Access token obtained successfully");

            // Only refresh if refresh token is available
            if (accessToken.getRefreshToken() != null) {
                accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
                LOGGER.info("Access token refreshed successfully");
            }

            // Get user info
            OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, request);

            try (Response response = service.execute(request)) {
                if (response.getCode() == 200) {
                    LOGGER.info("User info retrieved successfully");
                    // Parse and store the user info
                    JSONObject userInfoJson = new JSONObject(response.getBody());
                    lastUserInfo = userInfoJson.toMap();
                    LOGGER.info("User info stored: " + userInfoJson.getString("email"));
                    return true;
                } else {
                    LOGGER.warning("Failed to get user info. Status code: " + response.getCode());
                    LOGGER.warning("Response body: " + response.getBody());
                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Authentication failed: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get the last retrieved user information from Google
     *
     * @return Map containing user information (email, name, picture, etc.)
     */
    public static Map<String, Object> getLastUserInfo() {
        return lastUserInfo != null ? new HashMap<>(lastUserInfo) : null;
    }

    /**
     * Clear the stored user information
     */
    public static void clearUserInfo() {
        lastUserInfo = null;
    }
}

