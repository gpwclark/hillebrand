package org.uant.textservice.message;

import java.util.Map;

/*
 *
 */

public interface SendMessageHandler {
    Map<String, String> sendMessage(int key);
}
