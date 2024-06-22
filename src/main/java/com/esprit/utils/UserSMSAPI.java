package com.esprit.utils;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
public class UserSMSAPI {
    /** 
     * @param number
     * @param senderName
     * @param messageBody
     */
    public static void sendSMS(int number, String senderName, String messageBody) {
        VonageClient client = VonageClient.builder().apiKey("bf61ba81").apiSecret("BsA4inzyxBJDOCwk").build();
        TextMessage message = new TextMessage("Vonage APIs",
                "216" + number,
                messageBody
        );
        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);
        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }
}
