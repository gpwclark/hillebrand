package org.uant.textservice.message;

/*
 *
 */

public class ReceivedMessage {
    public final String sender;
    public final String body;
    public final long timestamp;
    public final int hash;

    public ReceivedMessage(String sender, String body, long timestamp){
        this.sender = sender;
        this.body = body;
        this.timestamp = timestamp;
        String strToHash = this.sender + this.body + Long.toString(timestamp);
        this.hash = strToHash.hashCode();
    }
}
