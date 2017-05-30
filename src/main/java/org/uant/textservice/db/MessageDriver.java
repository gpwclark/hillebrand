package org.uant.textservice.db;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.db.MessageDBO;

import java.util.ArrayList;

/*
 *
 */
public interface MessageDriver {
    void insertMessage(ReceivedMessage msg);
    void insertMessage(MessageDBO msgDBO);
    void updateMessage(MessageDBO msgDBO);
    MessageDBO getMessage(int hash);
    MessageDBO getMessage(long id);
    ArrayList<MessageDBO> getTable();
    void deleteMessagesOlderThan(long someTimeAgo);
}
