package org.uant.textservice.main;

import org.uant.textservice.main.TextService;

//TODO test usage limits: for integration testing
//     https://developers.google.com/admin-sdk/email-settings/limits

/**
 *
 * @author  George P. Clark
 * @version 0.1
 * @since   2017-03-01
 */

public class Main {

    /*
     * design, Textservice can be in one of 3 states and transitions between
     * those states in order.
     * 1. msgDb is empty and the program is idle.
     * 2. resource processed but not in sent.
     * 3. resource sent and waiting for time to run before it is deleted.
     */

  public static void main(String[] args) {
    TextService service = new TextService();
    service.run();
  }
}
