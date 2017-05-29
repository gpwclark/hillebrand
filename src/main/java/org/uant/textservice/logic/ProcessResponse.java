package org.uant.textservice.logic;

import org.uant.textservice.db.ResourceDb;
import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MessageDBO;

import org.uant.textservice.logic.ResponseHandler;
import java.util.regex.Pattern;
import java.util.Map;

//TODO is it valid if a customer puts "status" in subject?

// if customer exists continue,
// else return invalid customer string
//
// if message says status continue
// else return invalid command
//
// else get customer data from db and put in response
public class ProcessResponse {
    ResourceDb resourceDb;
    InboxHandler msgDb;
    ResponseHandler rh;

    public ProcessResponse(ResourceDb resourceDb, InboxHandler msgDb) {
        this.resourceDb = resourceDb;
        this.msgDb = msgDb;
        rh = new ResponseHandler(resourceDb);
    }


    public MessageDBO processMessageResponse(int key) {
        MessageDBO record = msgDb.getMessage(key);
        String sender = record.getSender();
        String body = record.getBody();
        String response = rh.getResponse(sender, body);

        //TODO in truth if it is in invalid resource it doesn't matter if it is
        // a valid request or not?
        if (resourceDb.isValidCustomer(sender)) {
            record.setValidResource(true);
        } else {
            record.setValidResource(false);
        }

        if (rh.isValidRequest(body)) {
            record.setValidRequest(true);
        } else {
            record.setValidRequest(false);
        }

        record.setResponse(response);
        record.setProcessed(true);

        return record;
    }
}
