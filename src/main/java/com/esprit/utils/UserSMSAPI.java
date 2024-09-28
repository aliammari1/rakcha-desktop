package com.esprit.utils;

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
        final VonageClient client = VonageClient.builder().apiKey("bf61ba81").apiSecret("BsA4inzyxBJDOCwk").build();
        final TextMessage message = new TextMessage("Vonage APIs",
                "216" + number,
                messageBody);
        final SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
        if (MessageStatus.OK == response.getMessages().get(0).getStatus()) {
            UserSMSAPI.LOGGER.info("Message sent successfully.");
        } else {
            UserSMSAPI.LOGGER.info("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }

}
