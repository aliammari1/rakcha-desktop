package com.esprit.utils;

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

public class SignInMicrosoft {

    private static final String PROTECTED_RESOURCE_URL = "https://apis.live.net/v5.0/me";
    static OAuth20Service service;

    @SuppressWarnings("PMD.SystemPrintln")
    public static String SignInWithMicrosoft(String... args) throws IOException, InterruptedException, ExecutionException {
        // Replace these with your own api key and secret
        final String apiKey = "";
        final String apiSecret = "";
        service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .defaultScope("wl.basic")
                .callback("https://login.microsoftonline.com/common/oauth2/nativeclient")
                .build(LiveApi.instance());
        final Scanner in = new Scanner(System.in);

        System.out.println("=== Windows Live's OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        return authorizationUrl;
    }

    public static void verifyAuthUrl(String code) throws IOException, ExecutionException, InterruptedException {
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
//        final String code = in.nextLine();
        System.out.println();

        System.out.println("Trading the Authorization Code for an Access Token...");
        final OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        try (Response response = service.execute(request)) {
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");

    }
}