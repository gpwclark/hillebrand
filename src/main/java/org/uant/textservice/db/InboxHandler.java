package org.uant.textservice.db;

import org.uant.textservice.message.ReceivedMessage;

import java.util.Map;
/*
 *
 */

public interface InboxHandler {
    void storeMessage(ReceivedMessage msg);
    //TODO for testing
    void storeMessage(int key, Map<String, String> record);
    Map getMessage(int key);
}
