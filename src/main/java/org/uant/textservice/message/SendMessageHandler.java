package org.uant.textservice.message;

import java.util.Map;
import org.uant.textservice.db.MessageDBO;

/*
 *
 */

public interface SendMessageHandler {
    MessageDBO sendMessage(int key);
}
