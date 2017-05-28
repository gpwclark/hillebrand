package org.uant.textservice.message;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import java.util.Date;

/*
 *
 */

public class MockMessageGenerator implements ReceivedMessageHandler {
    Date date = new Date();

    public ReceivedMessage createMessage(String sender, String body) {
        final long timestamp = date.getTime();

        return new ReceivedMessage(sender, body, timestamp);
    }
}
