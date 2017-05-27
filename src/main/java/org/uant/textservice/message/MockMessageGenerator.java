package org.uant.textservice.message;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import java.util.Date;
import java.util.UUID;

/*
 *
 */

public class MockMessageGenerator implements ReceivedMessageHandler {
    Date date = new Date();

    public ReceivedMessage getMessage() {
        final String sender = UUID.randomUUID().toString();
        final String body = UUID.randomUUID().toString();
        final long timestamp = date.getTime();

        return new ReceivedMessage(sender, body, timestamp);
    }
}
