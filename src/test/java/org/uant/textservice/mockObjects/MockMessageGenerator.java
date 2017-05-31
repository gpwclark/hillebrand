package org.uant.textservice.mockObjects;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import java.util.Date;

/*
 *
 */

public class MockMessageGenerator implements ReceivedMessageHandler {
    Date date = new Date();
    int timeCalled = 0;
    String body = "aljdslskjdlkjldsjlsaSTATUSlkajaldskj";
    String body1 = "lakjdlksjdlksajdlanlkjwna;oiuw;nwnwwlkja;s?><M";
    String body2 = "aljdslskjdlkjldsjlsastatusssslkajaldskj";
    String sender = "resource10@localhost.com";
    String sender1 = "resource199@localhost.com";
    String sender2 = "invalid@email.com";
    String newSender;
    String newBody;

    public ReceivedMessage getMessage() {
        final long timestamp = date.getTime();
        if (timeCalled == 0) {
            newSender = sender;
            newBody = body;
        }else if (timeCalled == 1) {
            newSender = sender1;
            newBody = body1;
        }else {
            newSender = sender2;
            newBody = body2;
        }
        this.timeCalled+=1;

        System.out.println("sender " + newSender);
        System.out.println("body " + newBody);
        return new ReceivedMessage(newSender, newBody, timestamp);
    }
}
