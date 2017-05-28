package org.uant.textservice.logic;

import org.uant.textservice.db.ResourceDb;
import org.uant.textservice.db.InboxHandler;

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


  public Map<String, String> processMessageResponse(int key) {
      Map<String, String> record = msgDb.getMessage(key);
      String sender = record.get("sender");
      String body = record.get("body");
      String response = rh.getResponse(sender, body);

      //TODO in truth if it is in invalid resource it doesn't matter if it is
      // a valid request or not?
      if (resourceDb.isValidCustomer(sender)) {
        record.put("validResource", "true");
      } else {
        record.put("validResource", "false");
      }

      if (rh.isValidRequest(body)) {
        record.put("validRequest", "true");
      } else {
        record.put("validRequest", "false");
      }


      record.put("response", response);

      return record;
  }
}
