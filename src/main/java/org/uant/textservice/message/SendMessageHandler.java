package org.uant.textservice.message;

import org.uant.textservice.db.MessageDBO;

/*
 *
 */
public interface SendMessageHandler {
    MessageDBO sendMessage(MessageDBO message);
}
