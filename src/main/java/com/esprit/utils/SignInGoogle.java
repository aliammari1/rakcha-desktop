package com.esprit.utils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.httpclient.ahc.AhcHttpClientConfig;
import java.security.SecureRandom;

public class SignInGoogle {
        static final String GOOGLE_CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
        static final String GOOGLE_CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
        private static final SecureRandom RANDOM = new SecureRandom();
        static final String SECRET_STATE = "secret" + RANDOM.nextInt(999_999);
        private static final String NETWORK_NAME = "Google Async";
        private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
        static OAuth20Service service;
        private static final Logger LOGGER = Logger.getLogger(SignInGoogle.class.getName());

        /**
         * @return String
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws IOException
         */
        public static String signInWithGoogle() throws InterruptedException, ExecutionException, IOException {
                final HttpClientConfig clientConfig = new AhcHttpClientConfig(new DefaultAsyncHttpClientConfig.Builder()
                                .setMaxConnections(5)
                                .setRequestTimeout(Duration.ofMillis(10_000))
                                .setPooledConnectionIdleTimeout(Duration.ofMillis(1_000))
                                .setReadTimeout(Duration.ofMillis(1_000))
                                .build());
                service = new ServiceBuilder(GOOGLE_CLIENT_ID)
                                .apiSecret(GOOGLE_CLIENT_SECRET)
                                .defaultScope("profile") // replace with desired scope
                                .callback("urn:ietf:wg:oauth:2.0:oob")
                                .httpClientConfig(clientConfig)
                                .build(GoogleApi20.instance());
                LOGGER.info("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
                // Obtain the Authorization URL
                LOGGER.info("Fetching the Authorization URL...");
                // pass access_type=offline to get refresh token
                // https://developers.google.com/identity/protocols/OAuth2WebServer#preparing-to-start-the-oauth-20-flow
                final Map<String, String> additionalParams = new HashMap<>();
                additionalParams.put("access_type", "offline");
                // force to reget refresh token (if user are asked not the first time)
                additionalParams.put("prompt", "consent");
                final String authorizationUrl = service.createAuthorizationUrlBuilder()
                                .state(SECRET_STATE)
                                .additionalParams(additionalParams)
                                .build();
                LOGGER.info("Got the Authorization URL!");
                LOGGER.info("Now go and authorize ScribeJava here:");
                return authorizationUrl;
        }

        /**
         * @param code
         * @return Boolean
         * @throws IOException
         * @throws ExecutionException
         * @throws InterruptedException
         */
        public static Boolean verifyAuthUrl(String code) throws IOException, ExecutionException, InterruptedException {
                try {
                        LOGGER.info("And paste the state from server here. We have set 'secretState'='"
                                        + SECRET_STATE + "'.");
                        LOGGER.info(">>");
                        // final String value = in.nextLine();
                        // if (secretState.equals(value)) {
                        // LOGGER.info("State value does match!");
                        // } else {
                        // LOGGER.info("Ooops, state value does not match!");
                        // LOGGER.info("Expected = " + secretState);
                        // LOGGER.info("Got = " + value);
                        // LOGGER.info();
                        // }
                        LOGGER.info("Trading the Authorization Code for an Access Token...");
                        OAuth2AccessToken accessToken = service.getAccessToken(String.valueOf(code));
                        LOGGER.info("Got the Access Token!");
                        LOGGER.info("(The raw response looks like this: " + accessToken.getRawResponse()
                                        + "')");
                        LOGGER.info("Refreshing the Access Token...");
                        accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
                        LOGGER.info("Refreshed the Access Token!");
                        LOGGER.info("(The raw response looks like this: " + accessToken.getRawResponse()
                                        + "')");
                        // Now let's go and ask for a protected resource!
                        LOGGER.info("Now we're going to access a protected resource...");
                        // while (true) {
                        LOGGER.info(
                                        "Paste fieldnames to fetch (leave empty to get profile, 'exit' to stop example)");
                        LOGGER.info(">>");
                        // final String query = in.nextLine();
                        final String requestUrl;
                        // if ("exit".equals(query)) {
                        // break;
                        // } else if (query == null || query.isEmpty()) {
                        requestUrl = PROTECTED_RESOURCE_URL;
                        // } else {
                        // requestUrl = PROTECTED_RESOURCE_URL + "?fields=" + query;
                        // }
                        final OAuthRequest request = new OAuthRequest(Verb.GET, requestUrl);
                        service.signRequest(accessToken, request);
                        try (Response response = service.execute(request)) {
                                LOGGER.info("the login information: ");
                                LOGGER.info(String.valueOf(response.getCode()));
                                LOGGER.info(response.getBody());
                        }
                        // }
                } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                return false;
        }
}
