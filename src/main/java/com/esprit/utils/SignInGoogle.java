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
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

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
    static final String GOOGLE_CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    static final String GOOGLE_CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    private static final SecureRandom RANDOM = new SecureRandom();
    static final String SECRET_STATE = "secret" + SignInGoogle.RANDOM.nextInt(999_999);
    private static final String NETWORK_NAME = "Google Async";
    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final Logger LOGGER = Logger.getLogger(SignInGoogle.class.getName());
    static OAuth20Service service;

    /**
     * @return String
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static String signInWithGoogle() throws InterruptedException, ExecutionException, IOException {
        HttpClientConfig clientConfig = new AhcHttpClientConfig(new DefaultAsyncHttpClientConfig.Builder()
                .setMaxConnections(5)
                .setRequestTimeout(Duration.ofMillis(10_000))
                .setPooledConnectionIdleTimeout(Duration.ofMillis(1_000))
                .setReadTimeout(Duration.ofMillis(1_000))
                .build());
        SignInGoogle.service = new ServiceBuilder(SignInGoogle.GOOGLE_CLIENT_ID)
                .apiSecret(SignInGoogle.GOOGLE_CLIENT_SECRET)
                .defaultScope("profile") // replace with desired scope
                .callback("urn:ietf:wg:oauth:2.0:oob")
                .httpClientConfig(clientConfig)
                .build(GoogleApi20.instance());
        SignInGoogle.LOGGER.info("=== " + SignInGoogle.NETWORK_NAME + "'s OAuth Workflow ===");
        // Obtain the Authorization URL
        SignInGoogle.LOGGER.info("Fetching the Authorization URL...");
        // pass access_type=offline to get refresh token
        // https://developers.google.com/identity/protocols/OAuth2WebServer#preparing-to-start-the-oauth-20-flow
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("access_type", "offline");
        // force to reget refresh token (if user are asked not the first time)
        additionalParams.put("prompt", "consent");
        String authorizationUrl = SignInGoogle.service.createAuthorizationUrlBuilder()
                .state(SignInGoogle.SECRET_STATE)
                .additionalParams(additionalParams)
                .build();
        SignInGoogle.LOGGER.info("Got the Authorization URL!");
        SignInGoogle.LOGGER.info("Now go and authorize ScribeJava here:");
        return authorizationUrl;
    }

    /**
     * @param code
     * @return Boolean
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static Boolean verifyAuthUrl(final String code) throws IOException, ExecutionException, InterruptedException {
        try {
            SignInGoogle.LOGGER.info("And paste the state from server here. We have set 'secretState'='"
                    + SignInGoogle.SECRET_STATE + "'.");
            SignInGoogle.LOGGER.info(">>");
            // final String value = in.nextLine();
            // if (secretState.equals(value)) {
            // LOGGER.info("State value does match!");
            // } else {
            // LOGGER.info("Ooops, state value does not match!");
            // LOGGER.info("Expected = " + secretState);
            // LOGGER.info("Got = " + value);
            // LOGGER.info();
            // }
            SignInGoogle.LOGGER.info("Trading the Authorization Code for an Access Token...");
            OAuth2AccessToken accessToken = SignInGoogle.service.getAccessToken(String.valueOf(code));
            SignInGoogle.LOGGER.info("Got the Access Token!");
            SignInGoogle.LOGGER.info("(The raw response looks like this: " + accessToken.getRawResponse()
                    + "')");
            SignInGoogle.LOGGER.info("Refreshing the Access Token...");
            accessToken = SignInGoogle.service.refreshAccessToken(accessToken.getRefreshToken());
            SignInGoogle.LOGGER.info("Refreshed the Access Token!");
            SignInGoogle.LOGGER.info("(The raw response looks like this: " + accessToken.getRawResponse()
                    + "')");
            // Now let's go and ask for a protected resource!
            SignInGoogle.LOGGER.info("Now we're going to access a protected resource...");
            // while (true) {
            SignInGoogle.LOGGER.info(
                    "Paste fieldnames to fetch (leave empty to get profile, 'exit' to stop example)");
            SignInGoogle.LOGGER.info(">>");
            // final String query = in.nextLine();
            String requestUrl;
            // if ("exit".equals(query)) {
            // break;
            // } else if (query == null || query.isEmpty()) {
            requestUrl = SignInGoogle.PROTECTED_RESOURCE_URL;
            // } else {
            // requestUrl = PROTECTED_RESOURCE_URL + "?fields=" + query;
            // }
            OAuthRequest request = new OAuthRequest(Verb.GET, requestUrl);
            SignInGoogle.service.signRequest(accessToken, request);
            try (final Response response = SignInGoogle.service.execute(request)) {
                SignInGoogle.LOGGER.info("the login information: ");
                SignInGoogle.LOGGER.info(String.valueOf(response.getCode()));
                SignInGoogle.LOGGER.info(response.getBody());
            }
            // }
        } catch (final Exception e) {
            SignInGoogle.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

}
