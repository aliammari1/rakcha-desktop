package com.esprit.services.evenements;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {
    public static final String ACCOUNT_SID = "ACbdf5d6feda24bb559b232d801c631a65";
    public static final String AUTH_TOKEN = "42c6d9e04357c619befa9276eb2c3eb7";

    public static void sendSms(String to, String message) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message sms = Message.creator(
                new PhoneNumber(to), // to
                new PhoneNumber("+14422540252"), // from
                message
        ).create();
    }
}
