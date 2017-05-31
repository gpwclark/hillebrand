package org.uant.textservice.mockObjects;

import org.uant.textservice.message.SendMessageHandler;
import org.uant.textservice.db.MessageDBO;
import java.util.Map;

/*
 *
 */
public class MockMessageSender implements SendMessageHandler {

    public MessageDBO sendMessage(MessageDBO message) {
        return message;
    }
}
