package org.uant.textservice.message;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.SendMessageHandler;
import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MessageDBO;
import java.util.Map;

/*
 *
 */

public class MockMessageSender implements SendMessageHandler {
    InboxHandler msgDb;

    public MockMessageSender(InboxHandler msgDb) {
        this.msgDb = msgDb;
    }

    public MessageDBO sendMessage(int key) {
        MessageDBO record = msgDb.getMessage(key);
        record.setSent(true);
        return record;
    }
}
