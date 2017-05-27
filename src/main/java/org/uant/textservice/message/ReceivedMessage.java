package org.uant.textservice.message;

/*
 *
 */

public class ReceivedMessage {
    public final String sender;
    public final String body;
    public final long timestamp;

    public ReceivedMessage(String sender, String body, long timestamp){
        this.sender = sender;
        this.body = body;
        this.timestamp = timestamp;
    }
}
