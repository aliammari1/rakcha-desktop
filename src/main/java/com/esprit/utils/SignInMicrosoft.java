package com.esprit.utils;

import com.github.scribejava.apis.LiveApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum SignInMicrosoft {
    ;
    private static final String PROTECTED_RESOURCE_URL = "https://apis.live.net/v5.0/me";
    private static final Logger LOGGER = Logger.getLogger(SignInMicrosoft.class.getName());
    private static OAuth20Service service;
    private static volatile Map<String, Object> lastUserInfo;

    /**
     * Initialize Microsoft Sign-In flow
     *
     * @param args Optional arguments (not used)
     * @return Authorization URL for the user to visit
     * @throws IOException           if there's an I/O error
     * @throws InterruptedException  if the operation is interrupted
     * @throws ExecutionException    if the operation fails
     * @throws IllegalStateException if required environment variables are missing
     */
    public static String SignInWithMicrosoft(final String... args)
        throws IOException, InterruptedException, ExecutionException {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String clientId = dotenv.get("MICROSOFT_CLIENT_ID");
        String clientSecret = dotenv.get("MICROSOFT_CLIENT_SECRET");

        if (clientId == null || clientSecret == null) {
            throw new IllegalStateException("Microsoft OAuth credentials not found in .env file");
        }

        service = new ServiceBuilder(clientId).apiSecret(clientSecret).defaultScope("wl.basic wl.emails")
            .callback("https://login.microsoftonline.com/common/oauth2/nativeclient").build(LiveApi.instance());

        LOGGER.info("=== Microsoft Live OAuth Workflow ===");
        String authorizationUrl = service.getAuthorizationUrl();
        LOGGER.info("Authorization URL generated successfully");
        return authorizationUrl;
    }

    /**
     * Complete the OAuth flow with the authorization code
     *
     * @param code The authorization code from Microsoft
     * @return boolean indicating if verification was successful
     * @throws IOException          if there's an I/O error
     * @throws ExecutionException   if the operation fails
     * @throws InterruptedException if the operation is interrupted
     */
    public static boolean verifyAuthUrl(final String code)
        throws IOException, ExecutionException, InterruptedException {
        try {
            if (service == null) {
                LOGGER.warning("OAuth service not initialized. Please call SignInWithMicrosoft() first.");
                return false;
            }

            if (code == null || code.trim().isEmpty()) {
                LOGGER.warning("Authorization code is empty or null");
                return false;
            }

            OAuth2AccessToken accessToken = service.getAccessToken(code);
            LOGGER.info("Access Token obtained successfully");

            // Get user info
            OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, request);

            try (Response response = service.execute(request)) {
                if (response.getCode() == 200) {
                    LOGGER.info("User info retrieved successfully");
                    // Parse and store the user info
                    JSONObject userInfoJson = new JSONObject(response.getBody());
                    lastUserInfo = userInfoJson.toMap();
                    LOGGER.info("User info stored: " + userInfoJson.optString("emails", "No email"));
                    return true;
                } else {
                    LOGGER.warning("Failed to get user info. Status code: " + response.getCode());
                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Authentication failed: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get the last retrieved user information from Microsoft
     *
     * @return Map containing user information (id, name, emails, etc.)
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
