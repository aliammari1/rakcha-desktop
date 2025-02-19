package com.esprit.utils;

import com.esprit.Config;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

import java.util.logging.Logger;

public enum UserSMSAPI {
    ;
    private static final Logger LOGGER = Logger.getLogger(UserSMSAPI.class.getName());

    /**
     * @param number
     * @param senderName
     * @param messageBody
     */
    public static void sendSMS(final int number, final String senderName, final String messageBody) {
        final Config config = Config.getInstance();
        final String apiKey = config.get("vonage.api.key");
        final String apiSecret = config.get("vonage.api.secret");

        if (apiKey == null || apiSecret == null) {
            LOGGER.severe("Vonage API credentials not found in config");
            return;
        }

        final VonageClient client = VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        final TextMessage message = new TextMessage("Vonage APIs",
                "216" + number,
                messageBody);

        final SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
        if (MessageStatus.OK == response.getMessages().get(0).getStatus()) {
            LOGGER.info("Message sent successfully.");
        } else {
            LOGGER.info("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }
}
