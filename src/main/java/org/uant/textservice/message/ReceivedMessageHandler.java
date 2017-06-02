package org.uant.textservice.message;

import org.uant.textservice.message.ReceivedMessage;
import java.util.ArrayList;

/*
 *
 */

public interface ReceivedMessageHandler {
    ArrayList<ReceivedMessage> getMessages();
}
