package org.uant.textservice.message;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.SendMessageHandler;
import org.uant.textservice.db.InboxHandler;
import java.util.Map;

/*
 *
 */

public class MockMessageSender implements SendMessageHandler {
    InboxHandler msgDb;

    public MockMessageSender(InboxHandler msgDb) {
        this.msgDb = msgDb;
    }

    public Map<String, String> sendMessage(int key) {
        Map<String, String> record = msgDb.getMessage(key);
        record.put("sent", "true");
        return record;
    }
}
