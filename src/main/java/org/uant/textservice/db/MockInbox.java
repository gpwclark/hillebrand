package org.uant.textservice.db;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.db.InboxHandler;

import java.util.Map;
import java.util.HashMap;

/*
 *
 */

public class MockInbox implements InboxHandler {
    Map<Integer, Map> db = new HashMap<Integer, Map>();

    public void storeMessage(ReceivedMessage msg) {
        Map<String, String> record = new HashMap<String, String>();
        record.put("sender", msg.sender);
        record.put("body", msg.body);
        record.put("validResource", "unknown");
        record.put("sent", "false");
        record.put("validRequest", "unknown");
        record.put("timestamp", Long.toString(msg.timestamp));
        db.put(msg.hash, record);
    }

    public void storeMessage(int key, Map<String, String> record){
        db.put(key, record);
    }
    public Map getMessage(int key){
        return db.get(key);
    }
}
