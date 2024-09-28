package com.esprit.utils;

import com.github.scribejava.apis.LiveApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public enum SignInMicrosoft {
    ;
    private static final String PROTECTED_RESOURCE_URL = "https://apis.live.net/v5.0/me";
    private static final Logger LOGGER = Logger.getLogger(SignInMicrosoft.class.getName());
    static OAuth20Service service;

    /**
     * @param args
     * @return String
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public static String SignInWithMicrosoft(final String... args)
            throws IOException, InterruptedException, ExecutionException {
        // Replace these with your own api key and secret
        String apiKey = System.getenv("MICROSOFT_CLIENT_ID");
        String apiSecret = System.getenv("MICROSOFT_CLIENT_SECRET");
        SignInMicrosoft.service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .defaultScope("wl.basic")
                .callback("https://login.microsoftonline.com/common/oauth2/nativeclient")
                .build(LiveApi.instance());
        Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        SignInMicrosoft.LOGGER.info("=== Windows Live's OAuth Workflow ===");
        // Obtain the Authorization URL
        SignInMicrosoft.LOGGER.info("Fetching the Authorization URL...");
        String authorizationUrl = SignInMicrosoft.service.getAuthorizationUrl();
        SignInMicrosoft.LOGGER.info("Got the Authorization URL!");
        SignInMicrosoft.LOGGER.info("Now go and authorize ScribeJava here:");
        return authorizationUrl;
    }

    /**
     * @param code
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void verifyAuthUrl(final String code) throws IOException, ExecutionException, InterruptedException {
        SignInMicrosoft.LOGGER.info("And paste the authorization code here");
        SignInMicrosoft.LOGGER.info(">>");
        // final String code = in.nextLine();
        SignInMicrosoft.LOGGER.info("Trading the Authorization Code for an Access Token...");
        OAuth2AccessToken accessToken = SignInMicrosoft.service.getAccessToken(code);
        SignInMicrosoft.LOGGER.info("Got the Access Token!");
        SignInMicrosoft.LOGGER.info("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        // Now let's go and ask for a protected resource!
        SignInMicrosoft.LOGGER.info("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, SignInMicrosoft.PROTECTED_RESOURCE_URL);
        SignInMicrosoft.service.signRequest(accessToken, request);
        try (final Response response = SignInMicrosoft.service.execute(request)) {
            SignInMicrosoft.LOGGER.info("Got it! Lets see what we found...");
            SignInMicrosoft.LOGGER.info(String.valueOf(response.getCode()));
            SignInMicrosoft.LOGGER.info(response.getBody());
        }
        SignInMicrosoft.LOGGER.info("Thats it man! Go and build something awesome with ScribeJava! :)");
    }

}
