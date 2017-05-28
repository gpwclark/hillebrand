package org.uant.textservice.logic;

import org.uant.textservice.db.ResourceDb;
import java.util.regex.Pattern;

//TODO is it valid if a customer puts "status" in subject?

// if customer exists continue,
// else return invalid customer string
//
// if message says status continue
// else return invalid command
//
// else get customer data from db and put in response
public class ResponseHandler {
    ResourceDb resourceDb;
    public ResponseHandler(ResourceDb resourceDb) {
        this.resourceDb = resourceDb;
    }


    public boolean isValidRequest(String body) {
        return containsCorrectQuery(body);
    }
    public String getResponse(String sender, String body) {

        if (containsCorrectQuery(body)) {
            //TODO need some sort of exception that if generated here  gets passed up
            //the stack, and handled in the main program. The main loop would have a
            //try catch in a while/true to handle this happening at any level.
            return resourceDb.getCustomerMessage(sender);
        } else {
            return "invalid query";
        }
    }

    public boolean containsCorrectQuery(String msgBody){
        return containsIgnoreCase(msgBody, "status");
    }

    private static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }
}
