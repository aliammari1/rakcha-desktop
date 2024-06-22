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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
public class SignInGoogle {
        // static final Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        static final String clientId = "";
        static final String clientSecret = "";
        static final String secretState = "secret" + new Random().nextInt(999_999);
        private static final String NETWORK_NAME = "Google Async";
        private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
        static OAuth20Service service;
        /** 
         * @return String
         * @throws InterruptedException
         * @throws ExecutionException
         * @throws IOException
         */
        public static String signInWithGoogle() throws InterruptedException, ExecutionException, IOException {
                final HttpClientConfig clientConfig = new AhcHttpClientConfig(new DefaultAsyncHttpClientConfig.Builder()
                                .setMaxConnections(5)
                                .setRequestTimeout(10_000)
                                .setPooledConnectionIdleTimeout(1_000)
                                .setReadTimeout(1_000)
                                .build());
                service = new ServiceBuilder(clientId)
                                .apiSecret(clientSecret)
                                .defaultScope("profile") // replace with desired scope
                                .callback("urn:ietf:wg:oauth:2.0:oob")
                                .httpClientConfig(clientConfig)
                                .build(GoogleApi20.instance());
                System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
                System.out.println();
                // Obtain the Authorization URL
                System.out.println("Fetching the Authorization URL...");
                // pass access_type=offline to get refresh token
                // https://developers.google.com/identity/protocols/OAuth2WebServer#preparing-to-start-the-oauth-20-flow
                final Map<String, String> additionalParams = new HashMap<>();
                additionalParams.put("access_type", "offline");
                // force to reget refresh token (if user are asked not the first time)
                additionalParams.put("prompt", "consent");
                final String authorizationUrl = service.createAuthorizationUrlBuilder()
                                .state(secretState)
                                .additionalParams(additionalParams)
                                .build();
                System.out.println("Got the Authorization URL!");
                System.out.println("Now go and authorize ScribeJava here:");
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
                        System.out.println();
                        System.out.println("And paste the state from server here. We have set 'secretState'='"
                                        + secretState + "'.");
                        System.out.print(">>");
                        // final String value = in.nextLine();
                        // if (secretState.equals(value)) {
                        // System.out.println("State value does match!");
                        // } else {
                        // System.out.println("Ooops, state value does not match!");
                        // System.out.println("Expected = " + secretState);
                        // System.out.println("Got = " + value);
                        // System.out.println();
                        // }
                        System.out.println("Trading the Authorization Code for an Access Token...");
                        OAuth2AccessToken accessToken = service.getAccessToken(String.valueOf(code));
                        System.out.println("Got the Access Token!");
                        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse()
                                        + "')");
                        System.out.println("Refreshing the Access Token...");
                        accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
                        System.out.println("Refreshed the Access Token!");
                        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse()
                                        + "')");
                        System.out.println();
                        // Now let's go and ask for a protected resource!
                        System.out.println("Now we're going to access a protected resource...");
                        // while (true) {
                        System.out.println(
                                        "Paste fieldnames to fetch (leave empty to get profile, 'exit' to stop example)");
                        System.out.print(">>");
                        // final String query = in.nextLine();
                        System.out.println();
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
                        System.out.println();
                        try (Response response = service.execute(request)) {
                                System.out.println("the login information: ");
                                System.out.println(response.getCode());
                                System.out.println(response.getBody());
                        }
                        System.out.println();
                        // }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return false;
        }
}
