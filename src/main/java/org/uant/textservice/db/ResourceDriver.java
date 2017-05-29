package org.uant.textservice.db;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.db.MessageDBO;

import java.util.Map;
/*
 *
 */

public interface ResourceDriver {
    boolean isValidResource(String resourceHandle);
    String getResourceMessage(String resourceHandle);
}
