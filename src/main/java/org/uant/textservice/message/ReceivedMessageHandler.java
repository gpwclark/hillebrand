package org.uant.textservice.message;

import org.uant.textservice.message.ReceivedMessage;

/*
 *
 */

public interface ReceivedMessageHandler {
    ReceivedMessage createMessage(String sender, String body);
}
