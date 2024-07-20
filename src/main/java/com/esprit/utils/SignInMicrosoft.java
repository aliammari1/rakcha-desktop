package com.esprit.utils;

import com.esprit.services.produits.AvisService;

import com.github.scribejava.apis.LiveApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class SignInMicrosoft {
    private static final String PROTECTED_RESOURCE_URL = "https://apis.live.net/v5.0/me";
    static OAuth20Service service;
    private static final Logger LOGGER = Logger.getLogger(SignInMicrosoft.class.getName());

    /**
     * @param args
     * @return String
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public static String SignInWithMicrosoft(String... args)
            throws IOException, InterruptedException, ExecutionException {
        // Replace these with your own api key and secret
        final String apiKey = System.getenv("MICROSOFT_CLIENT_ID");
        final String apiSecret = System.getenv("MICROSOFT_CLIENT_SECRET");
        service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .defaultScope("wl.basic")
                .callback("https://login.microsoftonline.com/common/oauth2/nativeclient")
                .build(LiveApi.instance());
        final Scanner in = new Scanner(System.in);
        LOGGER.info("=== Windows Live's OAuth Workflow ===");
        // Obtain the Authorization URL
        LOGGER.info("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        LOGGER.info("Got the Authorization URL!");
        LOGGER.info("Now go and authorize ScribeJava here:");
        return authorizationUrl;
    }

    /**
     * @param code
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void verifyAuthUrl(String code) throws IOException, ExecutionException, InterruptedException {
        LOGGER.info("And paste the authorization code here");
        LOGGER.info(">>");
        // final String code = in.nextLine();
        LOGGER.info("Trading the Authorization Code for an Access Token...");
        final OAuth2AccessToken accessToken = service.getAccessToken(code);
        LOGGER.info("Got the Access Token!");
        LOGGER.info("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        // Now let's go and ask for a protected resource!
        LOGGER.info("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        try (Response response = service.execute(request)) {
            LOGGER.info("Got it! Lets see what we found...");
            LOGGER.info(String.valueOf(response.getCode()));
            LOGGER.info(response.getBody());
        }
        LOGGER.info("Thats it man! Go and build something awesome with ScribeJava! :)");
    }

    private SignInMicrosoft() {
    }
}
