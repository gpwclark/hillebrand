package org.uant.textservice.message;

import java.util.Date;
/*
 *
 */

public class ReceivedMessage {
    public final String sender;
    public final String body;
    public final long timestamp;
    public final int hash;

    public ReceivedMessage(String sender, String body){
        this.sender = sender;
        this.body = body;
        //TODO static import?
        this.timestamp = new Date().getTime();
        String strToHash = this.sender + this.body + Long.toString(timestamp);
        this.hash = strToHash.hashCode();
    }
}
