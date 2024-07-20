package com.esprit.utils;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

import java.util.logging.Logger;

public class UserSMSAPI {
    private static final Logger LOGGER = Logger.getLogger(UserSMSAPI.class.getName());

    /**
     * @param number
     * @param senderName
     * @param messageBody
     */
    public static void sendSMS(int number, String senderName, String messageBody) {
        VonageClient client = VonageClient.builder().apiKey("bf61ba81").apiSecret("BsA4inzyxBJDOCwk").build();
        TextMessage message = new TextMessage("Vonage APIs",
                "216" + number,
                messageBody);
        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            LOGGER.info("Message sent successfully.");
        } else {
            LOGGER.info("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }

    private UserSMSAPI() {
    }
}
