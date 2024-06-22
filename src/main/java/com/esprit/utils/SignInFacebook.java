package com.esprit.utils;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
public class SignInFacebook {
    private static final String NETWORK_NAME = "Facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v3.2/me";
    /** 
     * @param args
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
        // Replace these with your client id and secret
        final String clientId = "3172803282854611";
        final String clientSecret = "808d0ff108a8459d656930871a986960";
        final String secretState = "secret" + new Random().nextInt(999_999);
        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback("https://aacoder.me")
                .build(FacebookApi.instance());
        final Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();
        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        final String authorizationUrl = service.getAuthorizationUrl(secretState);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();
        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }
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
