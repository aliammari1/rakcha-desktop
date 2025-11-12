package com.esprit.utils;

import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

public enum UserSMSAPI {
    ;
    private static final Logger LOGGER = Logger.getLogger(UserSMSAPI.class.getName());

    /**
     * @param number
     * @param senderName
     * @param messageBody
     */
    public static void sendSMS(final int number, final String senderName, final String messageBody) {
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        final String apiKey = dotenv.get("VONAGE_API_KEY");
        final String apiSecret = dotenv.get("VONAGE_API_SECRET");

        if (apiKey == null || apiSecret == null) {
            LOGGER.severe("Vonage API credentials not found in .env file");
            return;
        }


        final VonageClient client = VonageClient.builder().apiKey(apiKey).apiSecret(apiSecret).build();

        final TextMessage message = new TextMessage("Vonage APIs", "216" + number, messageBody);

        final SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
        if (MessageStatus.OK == response.getMessages().get(0).getStatus()) {
            LOGGER.info("Message sent successfully.");
        }
 else {
            LOGGER.info("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }

    }

}

